package statistics.service;

import statistics.core.IStatsCollector;
import statistics.core.ShardedStatsCollector;
import statistics.core.Stats;
import statistics.core.Transaction;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Exposes IStatsCollector as a thread safe component to the REST service.
 *
 * This approach gives a straightforward and relatively easy implementation
 * of a thread safe stats collector and separates concerns of threadsafe related
 * code and stats collecting algorithm.
 */
@Component("SyncStatsCollector")
public class SyncStatsCollector implements IStatsCollector {

    final IStatsCollector collector;

    final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public SyncStatsCollector() {
        this(new ShardedStatsCollector());
    }

    public SyncStatsCollector(IStatsCollector collector) {
        this.collector = collector;
    }

    @Override
    public boolean observe(Transaction txn) {
        try {
            rwLock.writeLock().lock();
            return collector.observe(txn);

        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public Stats getAggregated() {
        try {
            rwLock.readLock().lock();
            return collector.getAggregated();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
