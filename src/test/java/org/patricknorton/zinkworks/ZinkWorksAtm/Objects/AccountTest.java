package org.patricknorton.zinkworks.ZinkWorksAtm.Objects;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testHashCode() {
        Account expected = new Account( "123456789",  "1234",  800,  200);
        Account actual = new Account( "123456789",  "1234",  800,  200);

        Assert.assertTrue(expected.hashCode() == actual.hashCode());
    }
}