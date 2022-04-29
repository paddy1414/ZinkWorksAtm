package org.patricknorton.zinkworks.ZinkWorksAtm;

import java.util.Objects;

@SuppressWarnings("unused")
public class Account {
    private String accountId,pin;
    private int openingBalance,overDraft;

    public Account(String accountId, String pin, int openingBalance, int overDraft) {
        this.accountId = accountId;
        this.pin = pin;
        this.openingBalance = openingBalance;
        this.overDraft = overDraft;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return openingBalance == account.openingBalance && overDraft == account.overDraft && Objects.equals(accountId, account.accountId) && Objects.equals(pin, account.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, pin, openingBalance, overDraft);
    }
}
