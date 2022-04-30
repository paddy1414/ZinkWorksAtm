package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Transaction;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLLiteConnector {
    static int atmBalance = 1500;
    static
    LinkedHashMap<String, Integer> atmNoteBalance1 = new LinkedHashMap<>();
    Connection connection = null;

    Statement statement = null;
    private static SQLLiteConnector instance;

    public synchronized static SQLLiteConnector getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLLiteConnector();
            instance.insertBaseUser();
            // instance.fillAtm();
        }
        return instance;
    }


    private SQLLiteConnector() throws SQLException, ClassNotFoundException {
        setDb();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        //statement.executeUpdate("drop table if exists account");
        statement.executeUpdate("create table if not exists account (accountNum text primary key , pin text,openingBalance integer, overdraft integer)");
        statement.executeUpdate("create table if not exists atmBalance ( noteValue int primary key, count int)");
        statement.executeUpdate("create table if not exists recentTransaction ( accountNum text, timestamp long, openingBalance int, newBalance int, amount int )");
        //statement.getConnection().commit();
        //  statement.executeUpdate("insert into account values('123456789', '1234',800,200) SELECT '123456789', 'text to insert'  where not exists select 1 from account where accountNum = '123456789'");

    }

    public void insertBaseUser() throws SQLException {
        statement.executeUpdate("insert or ignore into account values('123456789', '1234',800,200) ");
        statement.executeUpdate("insert or ignore into account values('987654321', '4321',1230,150)");
    }

    public void resetBaseUsers() throws SQLException {
        statement.executeUpdate("update account set openingBalance =800 where accountNum = '123456789'");
        statement.executeUpdate("update account set openingBalance =1230 where accountNum = '987654321'");
    }

    public void resetATMNotes() throws SQLException {
        statement.executeUpdate("update atmBalance set count = 10 where noteValue = 50");
        statement.executeUpdate("update atmBalance set count = 30 where noteValue = 20");
        statement.executeUpdate("update atmBalance set count = 30 where noteValue = 10");
        statement.executeUpdate("update atmBalance set count = 20 where noteValue = 5");

    }


    private void fillAtm() throws SQLException {
        statement.executeUpdate("insert or ignore into atmBalance values(50, 10) ");
        statement.executeUpdate("insert or ignore into atmBalance values(20, 30)");
        statement.executeUpdate("insert or ignore into atmBalance values(10, 30)");
        statement.executeUpdate("insert or ignore into atmBalance values(5, 20)");

    }

    /**
     * @throws ClassNotFoundException
     */
    public void setDb() throws ClassNotFoundException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");
        try {
            String db = "jdbc:sqlite:zinkworks.db";
            System.out.println(db);
            // create a database connection
            connection = DriverManager.getConnection(db);
            statement = connection.createStatement();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null)
//                    connection.close();
//            }
//            catch(SQLException e)
//            {
//                // connection close failed.
//                System.err.println(e);
//            }
//        }
    }

    /**
     * @param accountNumber
     * @param pin
     * @return
     * @throws SQLException
     */
    public Account getAccount(String accountNumber, String pin) throws SQLException {
        String query = String.format("select * from account WHERE accountNum = %s AND pin = %s;", accountNumber, pin);
        ResultSet rs = statement.executeQuery(query);
        Account account = null;
        while (rs.next()) {
            // read the result set
            String accountId = rs.getString("accountNum");
            String pinFromAccount = rs.getString("pin");
            int openingBalance = rs.getInt("openingBalance");
            int overdraft = rs.getInt("overdraft");
            account = new Account(accountId, pinFromAccount, openingBalance, overdraft);
        }
        return account;
    }

    /**
     * @return
     * @throws SQLException
     */
    public List<Account> getAll() throws SQLException {
        String query = String.format("select * from account");
        ResultSet rs = statement.executeQuery(query);
        Account account = null;
        List<Account> accountList = new ArrayList<>();
        while (rs.next()) {
            // read the result set
            String accountId = rs.getString("accountNum");
            String pinFromAccount = rs.getString("pin");
            int openingBalance = rs.getInt("openingBalance");
            int overdraft = rs.getInt("overdraft");
            account = new Account(accountId, pinFromAccount, openingBalance, overdraft);
            accountList.add(account);
            System.out.println(account);

        }
        return accountList;
    }

    /**
     * @param accountNum
     * @param pin
     * @param amount
     * @return
     * @throws SQLException
     */
    public String withdrawMoney(String accountNum, String pin, int amount) throws SQLException {
        StringBuilder returnString = new StringBuilder();
        Account account = getAccount(accountNum, pin);
        if ((atmBalance) - amount > 0) {

            LinkedHashMap<String, Integer> notesDispensed = calculateNotesRemaining(amount);
            System.out.println("NotesDispensed: " + notesDispensed);
            int amountActuallyTakenOut = calculateAmountDeducted(notesDispensed);
            int newBalance = account.getOpeningBalance() - amountActuallyTakenOut;

            if (newBalance > (-account.getOverDraft())) {
                System.out.println("New Balance " + newBalance);
                String updateQuery = String.format("UPDATE account SET openingBalance = %d WHERE accountNum = %s AND pin = %s", newBalance, account.getAccountNum(), account.getPin());
                statement.executeUpdate(updateQuery);
                returnString.append("Update successful\n");
                returnString.append(String.format("New Balance is: %d \n", newBalance));
                returnString.append(printoutNotesDispenced(notesDispensed));
                removeFromAtm(notesDispensed);
                insertIntoTransaction(account.getAccountNum(), account.getOpeningBalance(), newBalance, amount);

            } else {
                returnString.append("You have Exceeded your overdraft limit");
            }

        } else {
            returnString.append("ERROR: Not enough cash in the ATM");
        }
        return returnString.toString();
    }

    public void insertIntoTransaction(String accountNum, int oldBalance, int newBalance, int amount) throws SQLException {
        String update = "insert into recentTransaction values (%s, %d, %d, %d, %d)";
        statement.executeUpdate(String.format(update, accountNum, System.currentTimeMillis(), oldBalance, newBalance, amount));
    }

    public List<Transaction> readTransaction(String accountNum) throws SQLException {
        String query = "select * from recentTransaction where accountNum = %s";
        List<Transaction> transactionList = new ArrayList<>();

        ResultSet rs = statement.executeQuery(String.format(query, accountNum));

        while (rs.next()) {
            int notesValue = rs.getInt("openingBalance");
            int newBalance = rs.getInt("newBalance");
            int amount = rs.getInt("amount");
            int timestamp = rs.getInt("timestamp");
            transactionList.add(new Transaction(notesValue, newBalance, amount, timestamp));
        }
        return transactionList;

    }

    public void removeFromAtm(LinkedHashMap<String, Integer> atmNoteBalance) {
        String query = "update atmBalance  set count = count- %d  where noteValue = %d";
        atmNoteBalance.keySet().stream().forEach(k -> {
            int noteValueInteger = Integer.parseInt(k);
            try {
                statement.executeUpdate(String.format(query, atmNoteBalance.get(k).intValue(), noteValueInteger));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This method will calculate how many notes are remaining in the ATM
     *
     * @param amountToWidraw
     * @return
     */
    public LinkedHashMap calculateNotesRemaining(int amountToWidraw) throws SQLException {
        AtomicInteger remainder = new AtomicInteger(amountToWidraw);
        StringBuilder sb = new StringBuilder();

        LinkedHashMap<String, Integer> notesWidrawn = new LinkedHashMap<>();

        String query = "select * from atmBalance order by noteValue DESC";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            int notesValue = rs.getInt("noteValue");
            int count = rs.getInt("count");
            int notesToRemove;
            if (remainder.get() % notesValue > 0 && count > 0) {
                notesToRemove = remainder.get() / notesValue;
                //  atmNoteBalance.put(k, atmNoteBalance.get(k) - notesToRemove);
                remainder.set(remainder.get() % notesValue);
                if (notesToRemove != 0) {
                    notesWidrawn.put(notesValue + "", notesToRemove);
                }
                sb.append(String.format("%s euro notes: %d \n", notesValue, notesToRemove));
            } else if (remainder.get() % notesValue == 0 && count > 0) {
                //  atmNoteBalance.put(k, atmNoteBalance.get(k) - notesToRemove);
                if ((remainder.get() / notesValue) < count) {
                    notesToRemove = remainder.get() / notesValue;
                    remainder.set(remainder.get() % notesValue);
                } else {
                    notesToRemove = count;
                    remainder.set(remainder.get() - (count * notesValue));
                }
                if (notesToRemove != 0) {
                    notesWidrawn.put(notesValue + "", notesToRemove);
                }

            }
        }
        ;

        sb.append(String.format("NOTE: %d euro is too small for us to withdraw", remainder.get()));

        notesWidrawn.keySet().forEach(k -> System.out.println(k));
        return notesWidrawn;
    }

    public int calculateAmountDeducted(LinkedHashMap<String, Integer> atmNotesRemoved) {
        final int[] remainingBalance = {0};
        atmNotesRemoved.keySet().forEach(k -> {
            int keyValue = Integer.parseInt(k);
            remainingBalance[0] = remainingBalance[0] + (atmNotesRemoved.get(k).intValue() * keyValue);
        });
        System.out.println("amaountDeducted: " + remainingBalance[0]);
        return remainingBalance[0];
    }

    public String printoutNotesDispenced(LinkedHashMap<String, Integer> notesDispenced) {
        StringBuilder sb = new StringBuilder();
        notesDispenced.keySet().stream().forEach(k -> sb.append(String.format("%s euro notes: %d \n", k, notesDispenced.get(k))));
        return sb.toString();
    }

    public String notesRemainingInAtm() throws SQLException {
        System.out.println("notesRemainingInAtm");
        StringBuilder sb = new StringBuilder();
        ResultSet rs = statement.executeQuery("select * from atmBalance");
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int notesValue = rs.getInt("noteValue");
            int count = rs.getInt("count");
            sb.append(String.format("\n%d euro notes: %d ", notesValue, count));
        }

        return sb.toString();
    }


    public String getMaxWithdrawal(int openingBalance, int overdraft) {
        StringBuilder sb = new StringBuilder();
        int maximumWithrdrawal = openingBalance + overdraft;
        int balaceAfterMax = openingBalance - overdraft;
        sb.append(String.format("your Maximum withdrawal is %d", maximumWithrdrawal));
        sb.append((balaceAfterMax < 0 ? String.format("this would leave you in arears of %d", balaceAfterMax) : "\nHave a wonderful day"));

        return sb.toString();
    }
}
