package statistics.core;

// Represents core info
public class Stats extends BaseStats {

    private double sum;

    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;

    public Stats(){
        Reset();
    }

    public final void Reset() {
        sum = 0;
        count = 0;

        min = Double.MAX_VALUE;
        max = -min;
    }

    public void observe(double value) {
        count++;
        sum += value;

        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void mergeWith(Stats stats) {
        sum += stats.getSum();
        count += stats.getCount();
        min = Math.min(min, stats.getMin());
        max = Math.max(max, stats.getMax());
    }

    public double getAvg() {
        if (getCount() == 0) {
            return 0;
        }
        return getSum() / getCount();
    }

    public double getSum() {
        return sum;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

}
