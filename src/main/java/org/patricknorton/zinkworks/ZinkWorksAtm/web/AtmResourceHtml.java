package org.patricknorton.zinkworks.ZinkWorksAtm.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Transaction;
import org.patricknorton.zinkworks.ZinkWorksAtm.RestOperations;
import org.patricknorton.zinkworks.ZinkWorksAtm.SQLLiteConnector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class AtmResourceHtml {
    String baseUrl = "http://localhost:8085/api/";


    /**
     * This will return the account information for eachh user
     *
     * @param accountNum String account number for user
     * @param pin        Personal Identity Number for the user
     * @param model      model
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String login(@RequestParam String accountNum, @RequestParam String pin, Model model) throws IOException {
        String returnPage;
        Account account = getAccount(accountNum, pin);

        List<Transaction> transactionList = getTransactions(accountNum);
        if (account == null) {
            returnPage = "noUser";
        } else {
            model.addAttribute("account", account);
            model.addAttribute("newLineChar", '\n');
            model.addAttribute("message", "\nHave a wonderful day \n");
            model.addAttribute("transactionList", transactionList);

            returnPage = "profile";
        }

        return returnPage;
    }

    private Account getAccount(String accountNum, String pin) throws IOException {
        JSONObject jsonProfile = RestOperations.getInstance().restCall("POST", String.format(baseUrl + "profile?accountNum=%s&pin=%s", accountNum, pin)).getJsonObject();
       return new Gson().fromJson(jsonProfile.toString(), Account.class);

    }
    private List<Transaction> getTransactions(String accountNum) throws IOException {
        JsonArray jsonTransactionList = RestOperations.getInstance().restCall("GET", String.format(baseUrl + "transactions?accountNum=%s", accountNum)).getJsonArray();
        List<Transaction> transactionList = new Gson().fromJson(jsonTransactionList.toString(), new TypeToken<List<Transaction>>(){}.getType());

        return transactionList.subList(0,10);
    }


    @RequestMapping(value = "/maxWithdrawal", method = RequestMethod.GET)
    public String maxWithdrawal(@RequestParam String accountNum, @RequestParam String pin, Model model) {

        String returnPage = "error";
        try {
            Account account = getAccount(accountNum, pin);
            List<Transaction> transactionList = getTransactions(accountNum);

             JSONObject jsonObject = RestOperations.getInstance().restCall("GET", String.format(baseUrl + "maxWithdrawal?accountNum=%s&pin=%s", accountNum, pin)).getJsonObject();
            if (account == null) {
                returnPage = "noUser";
            } else {
                model.addAttribute("account", account);
                model.addAttribute("newLineChar", '\n');
                model.addAttribute("message", jsonObject.get("message"));
                model.addAttribute("transactionList", transactionList.stream().limit(10).toArray());

                returnPage = "profile";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throws SQLException, ClassNotFoundException, IOException {
        int parsedWidraw = Integer.parseInt(withdraw);
        Account account = getAccount(accountNum, pin);

       JSONObject  jsonObject = RestOperations.getInstance().restCall("POST", String.format(baseUrl + "withdraw?accountNum=%s&pin=%s&withdraw=%d", accountNum, pin,parsedWidraw)).getJsonObject();

        List<Transaction> transactionList = getTransactions(accountNum);
        model.addAttribute("account", account);
        model.addAttribute("transactionList", transactionList.stream().limit(10).toArray());
        model.addAttribute("message", jsonObject.get("result"));
        model.addAttribute("newLineChar", '\n');


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
