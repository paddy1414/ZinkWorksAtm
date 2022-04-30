package org.patricknorton.zinkworks.ZinkWorksAtm.Objects;

import java.util.Objects;

@SuppressWarnings("unused")
public class Account {
    private String accountNum,pin;
    private int openingBalance,overDraft;

    public Account(String accountId, String pin, int openingBalance, int overDraft) {
        this.accountNum = accountId;
        this.pin = pin;
        this.openingBalance = openingBalance;
        this.overDraft = overDraft;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountId) {
        this.accountNum = accountId;
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
        return "org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account{" +
                "accountNum=" + accountNum +
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
        return openingBalance == account.openingBalance && overDraft == account.overDraft && Objects.equals(accountNum, account.accountNum) && Objects.equals(pin, account.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNum, pin, openingBalance, overDraft);
    }
}
