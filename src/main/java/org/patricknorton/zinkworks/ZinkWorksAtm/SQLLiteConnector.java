package org.patricknorton.zinkworks.ZinkWorksAtm;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLLiteConnector {
    static int atmBalance = 1500;
    Map<String, Integer> atmNoteBalance = new HashMap<>();
    Connection connection = null;

    Statement statement = null;
    private static SQLLiteConnector instance;

    public synchronized static SQLLiteConnector getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLLiteConnector();
        }
        return instance;
    }


    private SQLLiteConnector() throws SQLException, ClassNotFoundException {
        setDb();
        reFillAtm();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        //statement.executeUpdate("drop table if exists account");
        statement.executeUpdate("create table if not exists account (accountNum integer, pin integer,openingBalance integer, overdraft integer)");
        //  statement.executeUpdate("insert into account values(123456789, 1234,800,200)");
        //   statement.executeUpdate("insert into account values(987654321, 4321,1230,150)");
    }

    /**
     * This refills the ATM everytime the server is started
     */
    private void reFillAtm() {
        atmNoteBalance.put("50", 10);
        atmNoteBalance.put("20", 30);
        atmNoteBalance.put("10", 30);
        atmNoteBalance.put("5", 20);
    }

    /**
     *
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
     *
     *
     * @param accountNumber
     * @param pin
     * @return
     * @throws SQLException
     */
    public Account getAccount(int accountNumber, int pin) throws SQLException {
        String query = String.format("select * from account WHERE accountNum = %d AND pin = %d;", accountNumber, pin);
        System.out.println(query);
        ResultSet rs = statement.executeQuery(query);
        Account account = null;
        while (rs.next()) {
            // read the result set
            System.out.println("accountNum = " + rs.getString("accountNum"));
            int accountId = rs.getInt("accountNum");
            int pinFromAccount = rs.getInt("pin");
            int openingBalance = rs.getInt("openingBalance");
            int overdraft = rs.getInt("overdraft");
            account = new Account(accountId, pinFromAccount, openingBalance, overdraft);
            //  System.out.println("pin = " + rs.getInt("pin"));
        }
        return account;
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public List<Account> getAll() throws SQLException {
        String query = String.format("select * from account");
        System.out.println(query);
        ResultSet rs = statement.executeQuery(query);
        Account account = null;
        List<Account> accountList = new ArrayList<>();
        while (rs.next()) {
            // read the result set
            int accountId = rs.getInt("accountNum");
            int pinFromAccount = rs.getInt("pin");
            int openingBalance = rs.getInt("openingBalance");
            int overdraft = rs.getInt("overdraft");
            account = new Account(accountId, pinFromAccount, openingBalance, overdraft);
            accountList.add(account);
            System.out.println(account);

        }
        return accountList;
    }

    /**
     *
     * @param accountNum
     * @param pin
     * @param amount
     * @return
     * @throws SQLException
     */
    public String withdrawMoney(int accountNum, int pin, int amount) throws SQLException {
        StringBuilder returnString = new StringBuilder();
        Account account = getAccount(accountNum, pin);
        if (atmBalance - amount > 0) {
            LinkedHashMap<String, Integer> notesDispenced = calculateNotesRemaining(amount);
            int amountActuallyTakenOut = calculateAmountDeducted(notesDispenced);
            int newBalance = account.getOpeningBalance() - amountActuallyTakenOut;

            if (newBalance > account.getOverDraft()) {
                System.out.println("New Balance " + newBalance);
                String updateQuery = String.format("UPDATE account SET openingBalance = %d WHERE accountNum = %d AND pin = %d", newBalance, account.getAccountId(), account.getPin());
                statement.executeUpdate(updateQuery);
                returnString.append( "Update successful\n");
                returnString.append(printoutNotesDispenced(notesDispenced));
            } else {
                returnString.append("You have Exceeded your overdraft limit");
            }

        } else {
            returnString.append( "ERROR: Not enough cash in the ATM");
        }
        return returnString.toString();
    }

    /**
     * This method will calculate how many notes are remaining in the ATM
     * @param amountToWidraw
     * @return
     */
    public LinkedHashMap calculateNotesRemaining(int amountToWidraw) {
        AtomicInteger remainder = new AtomicInteger(amountToWidraw);
        StringBuilder sb = new StringBuilder();
        LinkedHashMap<String, Integer> notesWidrawn = new LinkedHashMap<>();
        atmNoteBalance.keySet().stream().forEach(k -> {
            int keyAsInt = Integer.parseInt(k);
            int notesToRemove;
            if (remainder.get() % keyAsInt > 1 && atmNoteBalance.get(k) > (remainder.get()/keyAsInt)) {
                notesToRemove = remainder.get() / keyAsInt;
                atmNoteBalance.put(k, atmNoteBalance.get(k) - notesToRemove);
                remainder.set(remainder.get() % keyAsInt);
                if (notesToRemove != 0) {
                    notesWidrawn.put(k, notesToRemove);
                }
                sb.append(String.format("%s euro notes: %d \n", k, notesToRemove));
            }
        });

        sb.append(String.format("NOTE: %d euro is too small for us to widraw", remainder.get()));

        System.out.println("sb: " + sb);
        return notesWidrawn;
    }

    public int calculateAmountDeducted (LinkedHashMap<String, Integer> atmNotesRemoved) {
        final int[] remainingBalance = {0};
        atmNotesRemoved.keySet().forEach(k-> {
            int keyValue = Integer.parseInt(k);
            remainingBalance[0] = remainingBalance[0] +  (atmNotesRemoved.get(k).intValue() * keyValue);
        });
        return remainingBalance[0];
    }

    public String printoutNotesDispenced(LinkedHashMap<String, Integer> notesDispenced) {
        StringBuilder sb = new StringBuilder();
        notesDispenced.keySet().stream().forEach(k -> sb.append(String.format("%s euro notes: %d \n", k, notesDispenced.get(k))));
        return sb.toString();
    }

    public String notesRemainingInAtm () {
        StringBuilder sb = new StringBuilder();
        atmNoteBalance.keySet().stream().forEach(k -> sb.append(String.format("%s euro notes: %d \n", k, atmNoteBalance.get(k))));
        return sb.toString();
    }


}
