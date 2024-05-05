package service;

import daoservices.AccountsRepoService;
import model.accounts.Account;
import model.accounts.CheckingAccount;
import model.accounts.SavingsAccount;
import others.AuditManagement;

import java.sql.SQLException;
import java.util.Scanner;

public class AccountsService {
    private AccountsRepoService dbService;

    public AccountsService() throws SQLException {
        this.dbService = new AccountsRepoService();
    }

    public void create(Scanner scanner) {
        System.out.println("Enter the account type [savings/checking]:");
        String accountType = scanner.nextLine().toLowerCase();
        if (!accountType.equals("savings") && !accountType.equals("checking")) {
            return;
        }
        try {
            accountInit(scanner, accountType);
        } catch (SQLException e) {
            System.out.println("Account cannot be created " + e.getSQLState() + " " + e.getMessage());
        }
    }

    public void read(Scanner scanner) {
        System.out.println("Enter the account number:");
        String accountNumber = scanner.nextLine();
        try {
            dbService.getCheckingAccountByNumber(accountNumber);
            AuditManagement.writeToFile("Read checking account " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Account cannot be found " + e.getSQLState() + " " + e.getMessage());
        }
        try {
            dbService.getSavingsAccountByNumber(accountNumber);
            AuditManagement.writeToFile("Read savings account " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Account cannot be found " + e.getSQLState() + " " + e.getMessage());

        }

    }

    public void delete(Scanner scanner) {
        System.out.println("Enter the account number:");
        String accountNumber = scanner.nextLine();
        System.out.println("Enter the account type [savings/checking]:");
        String accountType = scanner.nextLine().toLowerCase();
        if (!accountType.equals("savings") && !accountType.equals("checking")) {
            return;
        }

        try {
            dbService.removeAccount(accountType, accountNumber);
            AuditManagement.writeToFile("Deleted account " + accountType + " " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Account cannot be deleted " + e.getSQLState() + " " + e.getMessage());
        }
    }

    public void update(Scanner scanner) {
        System.out.println("typeOfAccount:");
        String typeOfAccount = scanner.nextLine();
        if (!typeOfAccount.equals("savings") && !typeOfAccount.equals("checking")) {
            return;
        }
        System.out.println("Enter the account number:");
        String accountNumber = scanner.nextLine();
        Account account = dbService.getAccount(typeOfAccount, accountNumber);
        if (account == null) {
            return;
        }
        System.out.println("Enter the account holder name:");
        String name = scanner.nextLine();
        System.out.println("Enter the balance:");
        double balance = scanner.nextDouble();
        scanner.nextLine();


        account.setAccountHolder(name);
        account.setBalance(balance);
        account.setAccountNumber(accountNumber);
        if (typeOfAccount.equals("checking")) {
            checkingAccountInit(scanner, (CheckingAccount) account);
        }
        if (typeOfAccount.equals("savings")) {
            savingAccountInit(scanner, (SavingsAccount) account);
        }
        System.out.println("Account updated successfully! Welcome " + name + " with account number: " + accountNumber);
        try {
            dbService.updateAccount(account);
            AuditManagement.writeToFile("Updated account " + account);
        } catch (SQLException e) {
            System.out.println("Account cannot be updated " + e.getSQLState() + " " + e.getMessage());

        }
    }

    private void accountInit(Scanner scanner, String accountType) throws SQLException {
        System.out.println("Enter the account holder name:");
        String name = scanner.nextLine();
        double balance = 0;
        //we will generate a random account number
        long accountNumber = (long) (Math.random() * 10000000000000000L);
        String accountNumberString = String.valueOf(accountNumber);

        Account account = new Account(accountNumberString, name, balance);
        AuditManagement.writeToFile("Created account " + account );
        if (accountType.equals("checking")) {
            CheckingAccount checkingAccount = new CheckingAccount(account);
            checkingAccountInit(scanner, checkingAccount);
            account = checkingAccount;
        }
        if (accountType.equals("savings")) {
            SavingsAccount savingsAccount = new SavingsAccount(account);
            savingAccountInit(scanner, savingsAccount);
            account = savingsAccount;
        }

        try {
            dbService.addAccount(account);
            System.out.println("Account created successfully! Welcome " + name + " with account number: " + accountNumberString);
        } catch (SQLException e) {
            System.out.println("Account cannot be created " + e.getSQLState() + " " + e.getMessage());
        }
    }

    /*
    * private double overdraftLimit;
    private double transactionFee;
    private String debitCardNumber;
    * */
    private void checkingAccountInit(Scanner scanner, CheckingAccount account) {
        System.out.println("Enter the overdraft limit:");
        double overdraftLimit = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the transaction fee:");
        double transactionFee = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the debit card number:");
        String debitCardNumber = scanner.nextLine();
        //scanner.nextLine();

        account.setOverdraftLimit(overdraftLimit);
        account.setTransactionFee(transactionFee);
        account.setDebitCardNumber(debitCardNumber);


    }

    /*
    * private double interestRate;
    private double minimumBalance;
    private double penalty;
    * */
    private void savingAccountInit(Scanner scanner, SavingsAccount account) {

        System.out.println("Enter the interest rate:");
        double interestRate = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the minimum balance:");
        double minimumBalance = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the penalty:");
        double penalty = scanner.nextDouble();
        scanner.nextLine();

        account.setInterestRate(interestRate);
        account.setMinimumBalance(minimumBalance);
        account.setPenalty(penalty);
    }
}

