package org.patricknorton.zinkworks.ZinkWorksAtm.Objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    Transaction transaction;


    @Test
    void setOpeningBalance() {
        transaction = new Transaction(4567, 2000, 150, 14567890);
        transaction.setOpeningBalance(200);
        assertEquals(200,transaction.getOpeningBalance());
    }

    @Test
    void getNewBalance() {
        transaction = new Transaction(4567, 2000, 150, 14567890);
        assertEquals(2000,transaction.getNewBalance());
    }

    @Test
    void setNewBalance() {
        transaction = new Transaction(4567, 2000, 150, 14567890);
        transaction.setNewBalance(2552);
        assertEquals(2552,transaction.getNewBalance());
    }

}