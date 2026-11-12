
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BankAccount {

    static final Object TRANSACTION_LOCK = new Object();

    private final String accountNumber;
    private Money balance;
    private final Money overdraftLimit;
    private AccountStatus status;

    private final List<LedgerEntry> ledger = new ArrayList<>();

    // Constructor
    public BankAccount(String accountNumber,
                       Money openingBalance,
                       Money overdraftLimit) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid account number");
        }

        if (openingBalance == null || openingBalance.isNegative()) {
            throw new IllegalArgumentException(
                "Opening balance cannot be negative");
        }

        if (overdraftLimit == null || overdraftLimit.isNegative()) {
            throw new IllegalArgumentException(
                "Overdraft limit cannot be negative");
        }

        if (!openingBalance.getCurrency().equals(
                overdraftLimit.getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch");
        }

        this.accountNumber = accountNumber;
        this.balance = openingBalance;
        this.overdraftLimit = overdraftLimit;
        this.status = AccountStatus.ACTIVE;

        // Record opening balance only when positive
        if (openingBalance.isPositive()) {
            ledger.add(new LedgerEntry(
                UUID.randomUUID().toString(),
                "OPENING_BALANCE",
                openingBalance,
                "Account opened"
            ));
        }
    }

    // Deposit money
    public void deposit(Money amount) {
        synchronized (TRANSACTION_LOCK) {

            validateAmount(amount);

            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Account must be active to deposit");
            }

            balance = balance.add(amount);

            addLedgerEntry(
                "DEPOSIT", amount, "Money deposited");
        }
    }

    // Withdraw money
    public void withdraw(Money amount) {
        synchronized (TRANSACTION_LOCK) {

            validateAmount(amount);
            validateActive();

            Money newBalance = balance.subtract(amount);

            if (newBalance.compareTo(overdraftLimit.negate()) < 0) {
                throw new IllegalStateException(
                    "Overdraft limit exceeded");
            }

            balance = newBalance;

            addLedgerEntry(
                "WITHDRAWAL", amount, "Money withdrawn");
        }
    }

    // Freeze account
    public void freeze() {
        synchronized (TRANSACTION_LOCK) {

            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Only active accounts can be frozen");
            }

            status = AccountStatus.FROZEN;
        }
    }

    // Unfreeze account
    public void unfreeze() {
        synchronized (TRANSACTION_LOCK) {

            if (status != AccountStatus.FROZEN) {
                throw new IllegalStateException(
                    "Only frozen accounts can be unfrozen");
            }

            status = AccountStatus.ACTIVE;
        }
    }

    // Close account
    public void close() {
        synchronized (TRANSACTION_LOCK) {

            if (status == AccountStatus.CLOSED) {
                throw new IllegalStateException(
                    "Account is already closed");
            }

            if (!balance.isZero()) {
                throw new IllegalStateException(
                    "Balance must be zero before closing");
            }

            status = AccountStatus.CLOSED;
        }
    }

    // Validate transaction amount
    private void validateAmount(Money amount) {

        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException(
                "Transaction amount must be positive");
        }

        if (!balance.getCurrency().equals(
                amount.getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch");
        }
    }

    // Validate account status
    private void validateActive() {

        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Account is frozen or closed");
        }
    }

    // Add ledger entry
    private void addLedgerEntry(String type,
                                Money amount,
                                String description) {

        ledger.add(new LedgerEntry(
            UUID.randomUUID().toString(),
            type,
            amount,
            description
        ));
    }

    // Transfer validation
    void validateTransfer(Money amount) {
        validateAmount(amount);
        validateActive();

        Money newBalance = balance.subtract(amount);

        if (newBalance.compareTo(overdraftLimit.negate()) < 0) {
            throw new IllegalStateException(
                "Overdraft limit exceeded");
        }
    }

    void validateCredit(Money amount) {
        validateAmount(amount);

        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Destination account must be active");
        }
    }

    // Debit source account
    void applyDebit(Money amount, String transferId) {

        balance = balance.subtract(amount);

        ledger.add(new LedgerEntry(
            transferId,
            "TRANSFER_DEBIT",
            amount,
            "Money transferred to another account"
        ));
    }

    // Credit destination account
    void applyCredit(Money amount, String transferId) {

        balance = balance.add(amount);

        ledger.add(new LedgerEntry(
            transferId,
            "TRANSFER_CREDIT",
            amount,
            "Money received from another account"
        ));
    }

    // Methods used for rollback
    Money balanceSnapshot() {
        return balance;
    }

    int ledgerSizeSnapshot() {
        return ledger.size();
    }

    void restoreTransferState(Money oldBalance,
                              int oldLedgerSize) {

        balance = oldBalance;

        while (ledger.size() > oldLedgerSize) {
            ledger.remove(ledger.size() - 1);
        }
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        synchronized (TRANSACTION_LOCK) {
            return balance;
        }
    }

    public Money getOverdraftLimit() {
        return overdraftLimit;
    }

    public AccountStatus getStatus() {
        synchronized (TRANSACTION_LOCK) {
            return status;
        }
    }

    public List<LedgerEntry> getLedger() {
        synchronized (TRANSACTION_LOCK) {
            return Collections.unmodifiableList(
                new ArrayList<>(ledger));
        }
    }

    // Display transaction history
    public void displayLedger() {
        synchronized (TRANSACTION_LOCK) {

            System.out.println("\nTransaction History:");

            for (LedgerEntry entry : ledger) {
                System.out.println(entry);
            }
        }
    }
}
