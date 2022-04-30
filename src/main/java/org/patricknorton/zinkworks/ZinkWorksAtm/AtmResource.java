package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
public class AtmResource {

    /**
     * This will return the account information for eachh user
     *
     * @param accountNum String account number for user
     * @param pin        Personal Identity Number for the user
     * @param model      model
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String login(@RequestParam String accountNum, @RequestParam String pin, Model model) {

        String returnPage = "error";
        try {
            Account account = SQLLiteConnector.getInstance().getAccount(accountNum, pin);
            List<Transaction> transactionList = SQLLiteConnector.getInstance().readTransaction(accountNum);
            if (account == null) {
                returnPage = "noUser";
            } else {
                model.addAttribute("account", account);
                model.addAttribute("newLineChar", '\n');
                model.addAttribute("message", "\nHave a wonderful day \n");
                model.addAttribute("transactionList", transactionList);

                returnPage = "profile";
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return returnPage;
    }

    @RequestMapping(value = "/maxWithdrawal", method = RequestMethod.GET)
    public String maxWithdrawal(@RequestParam String accountNum, @RequestParam String pin, Model model) {

        String returnPage = "error";
        try {
            Account account = SQLLiteConnector.getInstance().getAccount(accountNum, pin);
            List<Transaction> transactionList = SQLLiteConnector.getInstance().readTransaction(accountNum);
            String maxWithdrawal = SQLLiteConnector.getInstance().getMaxWithdrawal(account.getOpeningBalance(), account.getOverDraft());
            if (account == null) {
                returnPage = "noUser";
            } else {
                System.out.println(account);
                model.addAttribute("account", account);
                model.addAttribute("newLineChar", '\n');
                model.addAttribute("message", maxWithdrawal);
                model.addAttribute("transactionList", transactionList);

                returnPage = "profile";
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return returnPage;
    }

    /**
     * This will do the withdrawal from the ATM
     *
     * @param accountNum String account number for user
     * @param pin        Personal Identity Number for the user
     * @param withdraw   amount the user wants to withdraw
     * @param model      model
     * @return Result from withdrawal
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public String getWindraw(@RequestParam String accountNum, @RequestParam String pin, @RequestParam String withdraw, Model model)
            throws SQLException, ClassNotFoundException {
        int parsedWidraw = Integer.parseInt(withdraw);
        String result = SQLLiteConnector.getInstance().withdrawMoney(accountNum, pin, parsedWidraw);

        Account account = SQLLiteConnector.getInstance().getAccount(accountNum, pin);
        List<Transaction> transactionList = SQLLiteConnector.getInstance().readTransaction(accountNum);

        model.addAttribute("account", account);
        model.addAttribute("message", result);
        model.addAttribute("newLineChar", '\n');
        model.addAttribute("transactionList", transactionList);


        return "profile";
    }

    /**
     * This will produce the current amaount of notes in the ATM
     *
     * @return Current notes in the ATM
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @GetMapping("/atmNoteBalance")
    public String atmNotesBalance(Model model)
            throws SQLException, ClassNotFoundException {
        String result = SQLLiteConnector.getInstance().notesRemainingInAtm();
        model.addAttribute("message", result);
        model.addAttribute("newLineChar", '\n');

        return "notesRemainingInAtm";
    }

}
