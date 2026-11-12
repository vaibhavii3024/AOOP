import java.util.UUID;

public class FundTransfer {

    private final String transferId;
    private final BankAccount source;
    private final BankAccount destination;
    private final Money amount;

    private boolean completed = false;

    public FundTransfer(BankAccount source,
                        BankAccount destination,
                        Money amount) {

        if (source == null || destination == null || amount == null) {
            throw new IllegalArgumentException("Invalid transfer details");
        }

        if (source == destination ||
            source.getAccountNumber().equals(
                destination.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Source and destination must be different");
        }

        if (!amount.isPositive()) {
            throw new IllegalArgumentException(
                "Transfer amount must be positive");
        }

        this.transferId = UUID.randomUUID().toString();
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    public void transfer() {

        synchronized (BankAccount.TRANSACTION_LOCK) {

            if (completed) {
                throw new IllegalStateException(
                    "Transfer already completed");
            }

            // Validate both accounts before changing balances
            source.validateTransfer(amount);
            destination.validateCredit(amount);

            Money sourceOldBalance = source.balanceSnapshot();
            Money destinationOldBalance =
                destination.balanceSnapshot();

            int sourceLedgerSize = source.ledgerSizeSnapshot();
            int destinationLedgerSize =
                destination.ledgerSizeSnapshot();

            try {

                source.applyDebit(amount, transferId);

                destination.applyCredit(amount, transferId);

                completed = true;

                System.out.println("Transfer Successful!");
                System.out.println("Transfer ID: " + transferId);
                System.out.println("Amount: " + amount);

            } catch (RuntimeException e) {

                // Restore both accounts
                source.restoreTransferState(
                    sourceOldBalance, sourceLedgerSize);

                destination.restoreTransferState(
                    destinationOldBalance,
                    destinationLedgerSize);

                System.out.println("Transfer Failed!");
                System.out.println("Transaction rolled back.");

                throw e;
            }
        }
    }

    public String getTransferId() {
        return transferId;
    }

    public boolean isCompleted() {
        return completed;
    }
}