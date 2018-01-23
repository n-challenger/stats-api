package statistics.core;

// Represents the transaction info.
public class Transaction {
    private double amount;

    private long timestamp;

    public Transaction() {

    }

    public Transaction(long timestamp, double amount){
        this.setAmount(amount);
        this.setTimestamp(timestamp);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
