import java.time.Instant;

public final class LedgerEntry {
    private final String transactionId;
    private final String transactionType;
    private final Money amount;
    private final Instant timestamp;
    private final String description;

    public LedgerEntry(String transactionId,
                       String transactionType,
                       Money amount,
                       String description) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Invalid transaction ID");
        }

        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.timestamp = Instant.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return transactionType + " | " + amount +
               " | " + timestamp + " | " + description;
    }
}