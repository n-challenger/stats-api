package statistics.core;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class StatsTest {

    private Stats stats;

    private static void observeMany(Stats s, double... values) {
        for (double v : values) {
            s.observe(v);
        }
    }

    @Before
    public void setUp(){
        stats = new Stats();
    }

    @Test
    public void testWithNegatives() {
        observeMany(stats, -2, -10);

        TestHelpers.assertStats(stats, 2, -2, -10, -12, -6);
    }

    @Test
    public void tesWithZeroObservations() {
        assertEquals(0, stats.getAvg(), TestHelpers.eps);
        assertEquals(0, stats.getCount());
        assertEquals(0, stats.getSum(), TestHelpers.eps);
    }

    @Test
    public void testObservePointAndCollection() {
        double[] values =  new double[]{5, 10, 3, 6, 2, 11};
        for (double val: values) {
            stats.observe(val);
        }

        TestHelpers.assertStats(stats, values.length, 11, 2, 37, 37.0 / values.length);
    }

    @Test
    public void testMergeWith() {
        observeMany(stats, 4, 10);
        TestHelpers.assertStats(stats, 2, 10, 4, 14,7);

        Stats target = new Stats();
        observeMany(target, 3, 7, 9);

        stats.mergeWith(target);

        TestHelpers.assertStats(stats, 5, 10, 3, 33, 33.0 / 5);
    }
}