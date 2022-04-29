package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Account testAccount = new Account("123456789", "1234", 800, 200);
        Account accountTrue = SQLLiteConnector.getInstance().getAccount("123456789", "1234");
        Account accountNull = SQLLiteConnector.getInstance().getAccount("34567", "1234");

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
    void calculateNotesRemaining() {
        System.out.println("Test calculateNotesRemaining");

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
}