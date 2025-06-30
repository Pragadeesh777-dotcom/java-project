import java.sql.*;
import java.util.*;

class Account {
    private String accountNumber;
    private String holderName;
    private double balance;

    public Account(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount);
        } else if (amount > balance) {
            System.out.println("Insufficient balance!");
        } else {
            System.out.println("Invalid withdrawal amount!");
        }
    }

    public void transfer(Account receiver, double amount) {
        if (receiver == null) {
            System.out.println("Receiver account not found!");
            return;
        }
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            receiver.deposit(amount);
            System.out.println("Transferred " + amount + " to " + receiver.holderName);
        } else if (amount > balance) {
            System.out.println("Insufficient balance for transfer!");
        } else {
            System.out.println("Invalid transfer amount!");
        }
    }

    public void displayBalance() {
        System.out.println("Balance: " + balance);
    }
}

class Bank {
    private Connection connection;

    public Bank() {
        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankManagementSystem", "root", "12345678");
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public void openAccount(String accountNumber, String holderName, double initialDeposit) {
        try {
            String sql = "INSERT INTO Accounts (accountNumber, holderName, balance) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, holderName);
            pstmt.setDouble(3, initialDeposit);
            pstmt.executeUpdate();
            System.out.println("Account opened for " + holderName);
        } catch (SQLException e) {
            System.out.println("Failed to open account!");
            e.printStackTrace();
        }
    }

    public Account getAccount(String accountNumber) {
        try {
            String sql = "SELECT * FROM Accounts WHERE accountNumber = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("accountNumber"), rs.getString("holderName"), rs.getDouble("balance"));
            } else {
                System.out.println("Account not found!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve account!");
            e.printStackTrace();
        }
        return null;
    }

    public void updateAccount(Account account) {
        try {
            String sql = "UPDATE Accounts SET balance = ? WHERE accountNumber = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update account!");
            e.printStackTrace();
        }
    }

    public void viewAllAccounts() {
        try {
            String sql = "SELECT * FROM Accounts";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Bank Accounts:");
            while (rs.next()) {
                System.out.println("Account Number: " + rs.getString("accountNumber") +
                        " | Holder: " + rs.getString("holderName") +
                        " | Balance: " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve accounts!");
            e.printStackTrace();
        }
    }
}

public class BankManagementSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Bank Management System ---");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Balance");
            System.out.println("6. View All Accounts");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: // Open Account
                    System.out.print("Enter account number: ");
                    String accNumber = scanner.nextLine();
                    System.out.print("Enter holder name: ");
                    String holderName = scanner.nextLine();
                    System.out.print("Enter initial deposit: ");
                    double initialDeposit = scanner.nextDouble();
                    bank.openAccount(accNumber, holderName, initialDeposit);
                    break;

                case 2: // Deposit
                    System.out.print("Enter account number: ");
                    accNumber = scanner.nextLine();
                    Account account = bank.getAccount(accNumber);
                    if (account != null) {
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = scanner.nextDouble();
                        account.deposit(depositAmount);
                        bank.updateAccount(account);
                    }
                    break;

                case 3: // Withdraw
                    System.out.print("Enter account number: ");
                    accNumber = scanner.nextLine();
                    account = bank.getAccount(accNumber);
                    if (account != null) {
                        System.out.print("Enter withdrawal amount: ");
                        double withdrawalAmount = scanner.nextDouble();
                        account.withdraw(withdrawalAmount);
                        bank.updateAccount(account);
                    }
                    break;

                case 4: // Transfer
                    System.out.print("Enter sender account number: ");
                    String senderAccNumber = scanner.nextLine();
                    Account sender = bank.getAccount(senderAccNumber);
                    if (sender != null) {
                        System.out.print("Enter receiver account number: ");
                        String receiverAccNumber = scanner.nextLine();
                        Account receiver = bank.getAccount(receiverAccNumber);
                        System.out.print("Enter transfer amount: ");
                        double transferAmount = scanner.nextDouble();
                        sender.transfer(receiver, transferAmount);
                        bank.updateAccount(sender);
                        bank.updateAccount(receiver);
                    }
                    break;

                case 5: // View Balance
                    System.out.print("Enter account number: ");
                    accNumber = scanner.nextLine();
                    account = bank.getAccount(accNumber);
                    if (account != null) {
                        account.displayBalance();
                    }
                    break;

                case 6: // View All Accounts
                    bank.viewAllAccounts();
                    break;

                case 7: // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
