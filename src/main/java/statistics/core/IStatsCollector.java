package statistics.core;

public interface IStatsCollector {

    /**
     * Collects core from a given transaction and performs all validity check
     * @param txn Transaction object to collect core from
     * @return boolean returns false if transaction was rejected to collect core from
     */
    boolean observe(Transaction txn);

    /**
     * Collects aggregated core for the implemented period of time
     * @return Stats returns aggregated core
     */
    Stats getAggregated();
}
