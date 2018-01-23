package statistics.core;

import java.util.concurrent.TimeUnit;

/**
 * Just an auxiliary class to store the core object in the bucket
 */
class Bucket {
    private long timeShard = -1;

    private Stats value = new Stats();

    public boolean isEmpty() {
        return timeShard == -1;
    }

    public long getTimeShard() {
        return timeShard;
    }

    public void setTimeShard(long timeShard) {
        this.timeShard = timeShard;
    }

    public Stats getValue() {
        return value;
    }
}

/**
 * An implementation of IStatsCollector, collects Transaction core over last 60 seconds
 *
 * ShardedStatsCollector collects transaction core into one-millisecond buckets
 * This approach gives constant time and memory complexity to add / retrieve aggregated core.
 * One millisecond is the minimum available time resolution as defined by the problem statement,
 * so this implementation gives the best time precision possible.
 * However, the trade-off is that the constant is rather high(60000 buckets).
 * The code is easy to modify to get much better constant with a trade-off in time precision.
 */
public class ShardedStatsCollector implements IStatsCollector {

    final ITimeProvider timer;
    final Bucket[] buckets ;


    private final long maxTimeMS = TimeUnit.SECONDS.toMillis(60);

    private final int bucketsCount = (int)maxTimeMS;


    public ShardedStatsCollector() {
        this(new RealTimeProvider());
    }

    public ShardedStatsCollector(ITimeProvider timer) {
        this.timer = timer;

        // initialise buckets once, no any other significant memory allocations further
        buckets = new Bucket[bucketsCount];
        for (int i = 0; i < bucketsCount; i++) {
            buckets[i] = new Bucket();
        }
    }

    private long getCurrentTimeShard() {
        return timer.now();
    }

    @Override
    public boolean observe(Transaction txn) {

        if (timer.now() - txn.getTimestamp() > maxTimeMS){
            return false;
        }

        // It is not defined by requirements explicitly, how we should handle a
        // future transaction case. This case makes no sense in the regard of
        // providing "last 60 seconds of statistics". It clearly incorrect for the
        // given implementation as well. This is why I decided to treat it as an exception.
        if (txn.getTimestamp()  > timer.now() ){
            throw new IllegalArgumentException();
        }

        long currentTimeShard = getCurrentTimeShard();

        int currentBucket = (int)(currentTimeShard % bucketsCount);

        Bucket bucket = buckets[currentBucket];

        if (bucket.isEmpty() || currentTimeShard - bucket.getTimeShard() >= bucketsCount) {
            bucket.getValue().Reset();
            bucket.setTimeShard(currentTimeShard);
        }
        bucket.getValue().observe(txn.getAmount());

        return true;
    }

    @Override
    public Stats getAggregated() {
        Stats result = new Stats();

        long currentTimeShard = getCurrentTimeShard();

        for (Bucket bucket : buckets) {
            if (currentTimeShard-bucket.getTimeShard() > bucketsCount) {
                continue;
            }
            result.mergeWith(bucket.getValue());
        };
        return result;
    }
}
