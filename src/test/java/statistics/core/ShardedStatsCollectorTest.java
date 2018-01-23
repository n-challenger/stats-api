package statistics.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ShardedStatsCollectorTest {
    ShardedStatsCollector collector;
    MockTimeProvider timer = new MockTimeProvider();

    static final double unitAmount = 2;


    @Before
    public void setUp(){
        collector = new ShardedStatsCollector(timer);
    }

    @Test
    public void testEmpty() {
        Stats result = collector.getAggregated();
        assertEquals(0, result.getCount());
        assertEquals(0, result.getSum(), TestHelpers.eps);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithFutureTransaction() {
        // set the initial "absolute" time
        timer.secondsNow = 1;
        Transaction outdated = new Transaction(TimeUnit.SECONDS.toMillis(2), unitAmount);
        assert(collector.observe(outdated));
    }

    @Test
    public void testWithOutdatedTransaction() {
        // set the initial "absolute" time
        timer.secondsNow = 120;

        Transaction outdated = new Transaction(TimeUnit.SECONDS.toMillis(59), unitAmount);
        assertFalse(collector.observe(outdated));
        assertEquals(0, collector.getAggregated().getCount());

        // outdated only by 1 millisecond, this is our maximum time resolution
        outdated = new Transaction(TimeUnit.SECONDS.toMillis(60) - 1, unitAmount);
        assertFalse(collector.observe(outdated));
        assertEquals(0, collector.getAggregated().getCount());

        Transaction txn = new Transaction(TimeUnit.SECONDS.toMillis(60), unitAmount);
        assertTrue(collector.observe(txn));
        assertEquals(1, collector.getAggregated().getCount());
    }

    @Test
    public void testStatsCollectionWithBackdatedTransactions() {

        // set the initial "absolute" time
        timer.secondsNow = 60;

        long noise = 123;

        int[] timepointsInSeconds = new int[]{59, 30, 1};

        for (int second : timepointsInSeconds) {
            collector.observe(new Transaction(TimeUnit.SECONDS.toMillis(second) + noise, unitAmount));
        }

        Stats result = collector.getAggregated();
        assertEquals(3, result.getCount());
        assertEquals(3*unitAmount, result.getSum(), TestHelpers.eps);
    }

    @Test
    public void testStatsCollectionOverTheTime() {
        // this test is intentionally made super verbose in order
        // to be able to follow the time flow easily and make changes if needed

        // 1st second
        timer.secondsNow = 1;

        collector.observe(new Transaction(timer.now(), unitAmount));
        collector.observe(new Transaction(timer.now(), unitAmount));
        collector.observe(new Transaction(timer.now(), unitAmount));

        // 11th second
        timer.secondsNow += 10;
        collector.observe(new Transaction(timer.now(), unitAmount));

        // 21st second
        timer.secondsNow += 10;
        collector.observe(new Transaction(timer.now(), unitAmount));
        collector.observe(new Transaction(timer.now(), unitAmount));

        // 51st second
        timer.secondsNow += 30;
        collector.observe(new Transaction(timer.now(), unitAmount));
        collector.observe(new Transaction(timer.now() , unitAmount));

        Stats result = collector.getAggregated();
        TestHelpers.assertStats(result, 8, unitAmount, unitAmount, 16, unitAmount);

        // 61st second
        timer.secondsNow += 10;
        collector.observe(new Transaction(timer.now(), unitAmount));

        result = collector.getAggregated();
        assertEquals(6, result.getCount());
        assertEquals(12, result.getSum(), TestHelpers.eps);
        TestHelpers.assertStats(result, 6, unitAmount, unitAmount, 12, unitAmount);

        // 126th second, more than 2 minutes passed since the beginning:
        // take into account only new data
        timer.secondsNow += 65;
        collector.observe(new Transaction(timer.now(), unitAmount));

        result = collector.getAggregated();
        assertEquals(1, result.getCount());
        assertEquals(2, result.getSum(), TestHelpers.eps);

        // 187th second, more than 3 minutes passed since the beginning:
        // no core for the last minute we have
        timer.secondsNow += 61;
        result = collector.getAggregated();
        assertEquals(0, result.getCount());
        assertEquals(0, result.getSum(), TestHelpers.eps);
    }
}