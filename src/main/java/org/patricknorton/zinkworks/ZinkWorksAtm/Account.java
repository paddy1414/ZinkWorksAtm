package org.patricknorton.zinkworks.ZinkWorksAtm;

@SuppressWarnings("unused")
public class Account {
    private int accountId,pin,openingBalance,overDraft;

    public Account(int accountId, int pin, int openingBalance, int overDraft) {
        this.accountId = accountId;
        this.pin = pin;
        this.openingBalance = openingBalance;
        this.overDraft = overDraft;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(int openingBalance) {
        this.openingBalance = openingBalance;
    }

    public int getOverDraft() {
        return overDraft;
    }

    public void setOverDraft(int overDraft) {
        this.overDraft = overDraft;
    }

    @Override
    public String toString() {
        return "org.patricknorton.zinkworks.ZinkWorksAtm.Account{" +
                "accountId=" + accountId +
                ", pin=" + pin +
                ", openingBalance=" + openingBalance +
                ", overDraft=" + overDraft +
                '}';
    }
}
