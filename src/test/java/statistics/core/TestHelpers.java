package statistics.core;

import static org.junit.Assert.assertEquals;

public class TestHelpers {
    public static final double eps = 0.000000001;

    public static void assertStats(Stats stats, int count,
                            double max, double min, double sum, double avg) {
        assertEquals(count, stats.getCount());
        assertEquals( min, stats.getMin() , eps);
        assertEquals(max, stats.getMax(), eps);
        assertEquals(sum, stats.getSum(), eps);
        assertEquals(avg, stats.getAvg(), eps);
    }
}
