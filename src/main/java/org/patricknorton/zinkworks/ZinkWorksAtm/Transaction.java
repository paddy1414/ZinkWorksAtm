package org.patricknorton.zinkworks.ZinkWorksAtm;

public class Transaction {
    int openingBalance, newBalance, amount;
    long timestamp;
    public Transaction(int openingBalance, int newBalance, int amount, long timestamp) {
        this.openingBalance = openingBalance;
        this.newBalance = newBalance;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
}
