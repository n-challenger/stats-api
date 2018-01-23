package statistics.service;

import org.junit.Before;
import org.junit.Test;
import statistics.core.MockTimeProvider;
import statistics.core.ShardedStatsCollector;
import statistics.core.TestHelpers;
import statistics.core.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SyncStatsCollectorTest {

    SyncStatsCollector collector;

    MockTimeProvider timer = new MockTimeProvider();

    @Before
    public void setUp(){
        collector = new SyncStatsCollector(new ShardedStatsCollector(timer));
    }

    @Test
    public void testWithConcurrency() throws ExecutionException, InterruptedException {

        // fix the time to be sure we write to the same time bucket
        timer.secondsNow = 1;

        double totalAmount = 0;
        int pointsCount = 1000000;

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < pointsCount; i++) {
            double amount = i;
            totalAmount += amount;
            futures.add(executor.submit(() -> {
                collector.observe(new Transaction(timer.now(), amount));
                return 0;
            }));
        }

        for (Future<Integer> future: futures) {
            future.get();
        }

        TestHelpers.assertStats(collector.getAggregated(),
            pointsCount, pointsCount - 1, 0, totalAmount, totalAmount / pointsCount);
    }
}