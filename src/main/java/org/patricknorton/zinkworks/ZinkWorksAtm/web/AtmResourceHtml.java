package org.patricknorton.zinkworks.ZinkWorksAtm.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Account;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Transaction;
import org.patricknorton.zinkworks.ZinkWorksAtm.RestOperations;
import org.patricknorton.zinkworks.ZinkWorksAtm.SQLLiteConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/web")
public class AtmResourceHtml {
    String baseUrl = "http://localhost:8085/api/";
    Properties properties = new Properties();

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmResourceHtml.class.getSimpleName());


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
        setupProperties();
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
           postToKafka( account.toString(), "profile");

            returnPage = "profile";
        }

        return returnPage;
    }

    private Account getAccount(String accountNum, String pin) throws IOException {
        JSONObject jsonProfile = RestOperations.getInstance().restCall("POST", String.format(baseUrl + "profile?accountNum=%s&pin=%s", accountNum, pin)).getJsonObject();
        return new Gson().fromJson(jsonProfile.toString(), Account.class);

    }

    private List<Transaction> getTransactions(String accountNum) throws IOException {
        setupProperties();
        JsonArray jsonTransactionList = RestOperations.getInstance().restCall("GET", String.format(baseUrl + "transactions?accountNum=%s", accountNum)).getJsonArray();
        List<Transaction> transactionList = new Gson().fromJson(jsonTransactionList.toString(), new TypeToken<List<Transaction>>() {
        }.getType());
       postToKafka( transactionList.toString(), "getAccount");

        return transactionList.subList(0, 10);
    }


    @RequestMapping(value = "/maxWithdrawal", method = RequestMethod.GET)
    public String maxWithdrawal(@RequestParam String accountNum, @RequestParam String pin, Model model) {
        setupProperties();
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
               postToKafka( model.asMap().toString(), "maxWithdrawal");

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
        setupProperties();
        int parsedWidraw = Integer.parseInt(withdraw);
        Account account = getAccount(accountNum, pin);

        JSONObject jsonObject = RestOperations.getInstance().restCall("POST", String.format(baseUrl + "withdraw?accountNum=%s&pin=%s&withdraw=%d", accountNum, pin, parsedWidraw)).getJsonObject();

        List<Transaction> transactionList = getTransactions(accountNum);
        model.addAttribute("account", account);
        model.addAttribute("transactionList", transactionList.stream().limit(10).toArray());
        model.addAttribute("message", jsonObject.get("result"));
        model.addAttribute("newLineChar", '\n');

        StringBuilder sb = new StringBuilder();

       postToKafka( model.asMap().toString(), "withdraw");


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
        setupProperties();
        String result = SQLLiteConnector.getInstance().notesRemainingInAtm();
        model.addAttribute("message", result);
        model.addAttribute("newLineChar", '\n');
       postToKafka(result, "balance");
        return "notesRemainingInAtm";
    }


    private void  postToKafka(String message, String operation) {
        
        String topic = "ZinkworksAtm";
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        LOGGER.info(formatter.format(date));
        String key = String.format("id_%s_%s", operation, date);
        //send Data - Async operation
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, message);

        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
                // executes every time a record is successfully sent or an exception is thrown
                if (e == null) {
                    //record was send
                    LOGGER.info("receveived new New metaData \n" +
                            "topic: " + metadata.topic() + "\n" +
                            "Key:  " + producerRecord.key() + "\n" +
                            "partition: " + metadata.partition() + "\n" +
                            "Offset: " + metadata.offset() + "\n" +
                            "timestamp: " + metadata.timestamp() + "\n"

                    );
                }

            }

        });
        producer.flush();
        producer.close();
    }
    //flush and close the producer - Synchronous


    private void setupProperties() {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    }


}
