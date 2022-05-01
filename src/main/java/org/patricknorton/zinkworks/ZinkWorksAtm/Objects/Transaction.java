package org.patricknorton.zinkworks.ZinkWorksAtm.Objects;

import com.google.gson.annotations.Expose;

public class Transaction {
    @Expose
    int openingBalance, newBalance, amount;
    @Expose
    Long timestamp;
    public Transaction(int openingBalance, int newBalance, int amount, long timestamp) {
        this.openingBalance = openingBalance;
        this.newBalance = newBalance;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(int openingBalance) {
        this.openingBalance = openingBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "openingBalance=" + openingBalance +
                ", newBalance=" + newBalance +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
