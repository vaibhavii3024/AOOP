public class Main {

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println(" BANK ACCOUNT MANAGEMENT SYSTEM");
        System.out.println("================================");

        // Create two accounts
        BankAccount accountA = new BankAccount(
            "ACC1001",
            Money.ofRupees(10000),
            Money.ofRupees(2000)
        );

        BankAccount accountB = new BankAccount(
            "ACC1002",
            Money.ofRupees(5000),
            Money.ofRupees(1000)
        );

        System.out.println("\nInitial Balances:");
        System.out.println("Account A: " + accountA.getBalance());
        System.out.println("Account B: " + accountB.getBalance());

        // Deposit
        System.out.println("\n1. DEPOSIT");
        accountA.deposit(Money.ofRupees(2000));
        System.out.println("Balance A: " + accountA.getBalance());

        // Withdrawal
        System.out.println("\n2. WITHDRAWAL");
        accountA.withdraw(Money.ofRupees(1000));
        System.out.println("Balance A: " + accountA.getBalance());

        // Fund Transfer
        System.out.println("\n3. FUND TRANSFER");

        FundTransfer transfer = new FundTransfer(
            accountA,
            accountB,
            Money.ofRupees(5000)
        );

        transfer.transfer();

        System.out.println("Account A: " + accountA.getBalance());
        System.out.println("Account B: " + accountB.getBalance());

        // Freeze account
        System.out.println("\n4. FREEZE ACCOUNT");

        accountB.freeze();

        try {
            accountB.withdraw(Money.ofRupees(500));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Unfreeze
        System.out.println("\n5. UNFREEZE ACCOUNT");

        accountB.unfreeze();
        System.out.println("Account Status: " + accountB.getStatus());

        // Invalid transaction
        System.out.println("\n6. INVALID TRANSACTION");

        try {
            accountA.deposit(Money.ofRupees(0));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Overdraft test
        System.out.println("\n7. OVERDRAFT VALIDATION");

        try {
            accountA.withdraw(Money.ofRupees(50000));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Ledger
        System.out.println("\n8. TRANSACTION HISTORY");

        accountA.displayLedger();
        accountB.displayLedger();

        // Close a zero-balance account
        System.out.println("\n9. CLOSE ACCOUNT");

        BankAccount accountC = new BankAccount(
            "ACC1003",
            Money.ofRupees(0),
            Money.ofRupees(0)
        );

        accountC.close();

        System.out.println("Account C Status: " + accountC.getStatus());

        System.out.println("\n================================");
        System.out.println("       PROGRAM COMPLETED");
        System.out.println("================================");
    }
}