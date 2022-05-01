package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class SQLLiteConnectorTest {


    @Test
    void getInstance() throws SQLException, ClassNotFoundException {
        System.out.println("TestGetInstance");
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();

    }

    @Test
    void setDb() throws SQLException, ClassNotFoundException {
        System.out.println("testSetDb");

        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();

    }

    @Test
    void getAccount() throws SQLException, ClassNotFoundException {
        System.out.println("TestGetAccount");
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.resetBaseUsers();
        Account testAccount = new Account("123456789", "1234", 800, 200);
        Account accountTrue = sqlLiteConnector.getAccount("123456789", "1234");
        Account accountNull = sqlLiteConnector.getAccount("34567", "1234");

        Assertions.assertEquals(testAccount, accountTrue);
        Assertions.assertNull(accountNull);
    }

    @Test
    void getAll() {
        System.out.println("Test getAll");

        List<Account> testAccounts = new ArrayList<>();
        testAccounts.add(new Account("123456789", "1234", 800, 200));
        testAccounts.add(new Account("123456789", "1234", 800, 200));
        testAccounts.add(new Account("123456789", "1234", 800, 200));
    }

    @Test
    void withdrawMoney() {
        System.out.println("Test withdrawMoney");

    }

    @Test
    void calculateNotesRemainingSmall() throws SQLException, ClassNotFoundException {
        System.out.println("Test calculateNotesRemaining");
        LinkedHashMap<String, Integer> testTrue = new LinkedHashMap<>();
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.fillAtm();
        sqlLiteConnector.resetATMNotes();

        testTrue.put("20", 1);

        Assertions.assertEquals(testTrue, sqlLiteConnector.calculateNotesRemaining(21));
    }

    @Test
    void calculateNotesRemainingLarge() throws SQLException, ClassNotFoundException {
        System.out.println("Test calculateNotesRemaining");
        LinkedHashMap<String, Integer> testTrue = new LinkedHashMap<>();
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.fillAtm();
        sqlLiteConnector.resetATMNotes();

        testTrue.put("50", 10);
        testTrue.put("20", 25);

        Assertions.assertEquals(testTrue, sqlLiteConnector.calculateNotesRemaining(1000));
    }

    @Test
    void calculateAmountDeducted() {
        System.out.println("Test calculateAmountDeducted");

    }

    @Test
    void printoutNotesDispenced() {
        System.out.println("Test printoutNotesDispenced");

    }

    @Test
    void notesRemainingInAtm() {
        System.out.println("Test notesRemainingInAtm");

    }


    @Test
    void insertBaseUser() {
    }

    @Test
    void testSetDb() {
    }

    @Test
    void testGetAccount() {
    }

    @Test
    void testGetAll() throws SQLException, ClassNotFoundException {
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.resetBaseUsers();
        List<Account> expected = new ArrayList<>();
        expected.add(new Account( "123456789",  "1234",  800,  200));
        expected.add(new Account( "987654321",  "4321",  1230,  150));
        Assertions.assertEquals(expected, sqlLiteConnector.getAll());
    }

    @Test
    void testWithdrawMoneySmall() throws SQLException, ClassNotFoundException {
        String expected = "Update successful\nNew Balance is: 780 \n20 euro notes: 1 \n";
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.resetBaseUsers();
        sqlLiteConnector.fillAtm();
        sqlLiteConnector.resetATMNotes();
        String actual = sqlLiteConnector.withdrawMoney("123456789", "1234", 21);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testWithdrawMoneyLarge() throws SQLException, ClassNotFoundException {
        String expected = "Update successful\nNew Balance is: 750 \n50 euro notes: 1 \n";
        SQLLiteConnector sqlLiteConnector = SQLLiteConnector.getInstance();
        sqlLiteConnector.resetBaseUsers();
        sqlLiteConnector.fillAtm();
        sqlLiteConnector.resetATMNotes();
        String actual = sqlLiteConnector.withdrawMoney("123456789", "1234", 51);
        Assertions.assertEquals(expected, actual);
    }

}