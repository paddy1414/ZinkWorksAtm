package org.patricknorton.zinkworks.ZinkWorksAtm.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Transaction;
import org.patricknorton.zinkworks.ZinkWorksAtm.SQLLiteConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AtmResourceRest {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    /**
     * This will return the account information for eachh user
     *
     * @param accountNum String account number for user
     * @param pin        Personal Identity Number for the user
     * @return
     */
    @PostMapping(value = "/profile")
    public String login(@RequestParam String accountNum, @RequestParam String pin) {
        System.out.println(accountNum);
        Account account;
        JSONObject returnObject;
        try {
            account = SQLLiteConnector.getInstance().getAccount(accountNum, pin);
            if (account == null) {
                returnObject = new JSONObject().put("page", "noUser");
            } else {
                returnObject = new JSONObject(new Gson().toJson(account));

            }

        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
            returnObject = new JSONObject().put("page", "noUser");
        }

        System.out.println();
        return returnObject.toString();
    }

    @RequestMapping(value = "/maxWithdrawal", method = RequestMethod.GET)
    public String maxWithdrawal(@RequestParam String accountNum, @RequestParam String pin, Model model) {
        JSONObject jsonObject = new JSONObject();
        try {
            Account account = SQLLiteConnector.getInstance().getAccount(accountNum, pin);
            List<Transaction> transactionList = SQLLiteConnector.getInstance().readTransaction(accountNum);
            String maxWithdrawal = SQLLiteConnector.getInstance().getMaxWithdrawal(account.getOpeningBalance(), account.getOverDraft());
            if (account == null) {
                jsonObject.put("error", "No User");
            } else {
                jsonObject.put("message", maxWithdrawal);
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            jsonObject.put("message", "error occured");

        }


        return jsonObject.toString();
    }

    @RequestMapping(value = "/resetAccounts", method = RequestMethod.POST)
    public String resetAccount() {
        JSONObject jsonObject = new JSONObject();
        try {
            SQLLiteConnector.getInstance().resetBaseUsers();
            SQLLiteConnector.getInstance().resetATMNotes();


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            jsonObject.put("message", "error occured");

        }


        return jsonObject.toString();
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

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);

        return jsonObject.toString();
    }

    /**
     * This will do the withdrawal from the ATM
     *
     * @param accountNum String account number for user
     * @return Result from withdrawal
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public String getTransaction(@RequestParam String accountNum)
            throws SQLException, ClassNotFoundException {
        List<Transaction> transactionList = SQLLiteConnector.getInstance().readTransaction(accountNum);

        JSONArray jsonArray = new JSONArray();
        transactionList.stream().forEach(k -> jsonArray.put(new Gson().toJson(k)));

        JsonArray jsonElements = (JsonArray) new Gson().toJsonTree(transactionList);


        return jsonElements.toString();
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
