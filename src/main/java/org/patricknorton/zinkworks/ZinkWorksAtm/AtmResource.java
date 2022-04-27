package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.sql.SQLException;

@RestController
@Controller
public class AtmResource {

    /**
     * This will return the account information for eachh user
     * @param accountNum String account number for user
     * @param pin Personal Identity Number for the user
     * @param model model
     * @return
     */
    @GetMapping("/profile")
    public String login(@RequestParam String accountNum, @RequestParam String pin , Model model) {

        System.out.println("Hello World");
        String result = "Welcome!";

        int parsedAccountNum = Integer.parseInt(accountNum);
        int parsedPin = Integer.parseInt(pin);
        try {
            Account account = SQLLiteConnector.getInstance().getAccount(parsedAccountNum, parsedPin);
            result = result + "\n" + account;
            model.addAttribute("accountNum", account.getAccountId() );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }



        return result;
    }


    /**
     * This will do the withdrawal from the ATM
     * @param accountNum String account number for user
     * @param pin Personal Identity Number for the user
     * @param windraw amount the user wants to withdraw
     * @param model model
     * @return Result from withdrawal
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @GetMapping("/widraw")
    public String getWindraw(@RequestParam String accountNum, @RequestParam String pin, @RequestParam String windraw , Model model)
            throws SQLException, ClassNotFoundException {
        int parsedAccountNum = Integer.parseInt(accountNum);
        int parsedPin = Integer.parseInt(pin);
        int parsedWidraw = Integer.parseInt(windraw);
       String result =  SQLLiteConnector.getInstance().withdrawMoney(parsedAccountNum, parsedPin, parsedWidraw);
       return result;
    }

    /**
     * This will produce the current amaount of notes in the ATM
     * @return Current notes in the ATM
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @GetMapping("/atmNoteBalance")
    public String atmNotesBalance()
            throws SQLException, ClassNotFoundException {
       String result =  SQLLiteConnector.getInstance().notesRemainingInAtm();
       return result;
    }

}
