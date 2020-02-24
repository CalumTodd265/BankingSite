package your.bank.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import your.bank.Account;
import your.bank.Transaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataFetcher {

    private String token;
    private String username;
    private String password;

    private String host;
    private static final String ACCOUNTS = "/accounts";
    private static final String TRANSACTIONS = "/auth/transaction";
    private static final String FRAUDULENT_TRANSACTIONS = "/secure/fraud";

    private ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    public DataFetcher(String host){
        this.host = host;
        objectMapper = new ObjectMapper();
        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {

            private ObjectMapper jacksonObjectMapper = new ObjectMapper();

            @Override
            public <T> T readValue(String s, Class<T> aClass) {
                try {
                    return jacksonObjectMapper.readValue(s, aClass);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object o) {
                try {
                    return jacksonObjectMapper.writeValueAsString(o);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Gets the IDs of the fraudulent transactions from the remote API
     *
     * @return A list of the UUIDs of the fraudulent transactions.
     */
    public List<UUID> getFraudulentTransactionIDs() {
        try{
            HttpResponse<UUID[]> ids = Unirest.get(host+FRAUDULENT_TRANSACTIONS)
                    .queryString("token", token)
                    .header("Accept", "application/json")
                    .asObject(UUID[].class);
            return Arrays.asList(ids.getBody());
        } catch (UnirestException e){
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets the transactions from the remote API
     *
     * @return The List of Transactions
     */
    public List<Transaction> getTransactions(List<Account> accounts){
        try {
            HttpResponse<String> response = Unirest.get(host+TRANSACTIONS).basicAuth(username, password).asObject(String.class);
            return parseTransactions(response.getBody(), accounts);
        } catch (UnirestException e){
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets the accounts from the remote API.
     *
     * @return A list of Accounts.
     */
    public List<Account> getAccounts() {
        try {
            HttpResponse<Account[]> accounts = Unirest.get(host + ACCOUNTS).asObject(Account[].class);
            return Arrays.asList(accounts.getBody());
        } catch (UnirestException e){
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parses a json String containing transactions to POJO Transactions if given
     * a list of valid accounts with which to cross reference.
     *
     * @param jsonString
     * @return
     */
    private List<Transaction> parseTransactions(String jsonString, List<Account> accounts) {
        List<Transaction> transactions = new ArrayList<>();
        JSONArray jsonArray;
        // make sure to handle if the endpoint returns invalid json!
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e){
            logger.error(e.getMessage());
            return new ArrayList<>();
        }

        JSONObject current;
        Transaction transaction;

        for(int i = 0; i < jsonArray.length(); i++){
            current = jsonArray.getJSONObject(i);
            transaction = new Transaction(
                    current.getString("id"),
                    searchAccountsByName(accounts, current.getString("from")),
                    searchAccountsByName(accounts, current.getString("to")),
                    current.getBigDecimal("amount"));
            transactions.add(transaction);
        }

        return transactions;
    }

    /**
     * Sets the  authentication token
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Sets the Basic authorization username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the Basic authorization password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Search accounts by checking for equality of the name.
     *
     * It is extremely hacky that we have to have this here. However it is a result of the Transaction
     * class holding references to Account objects. Could refactor in future.
     *
     * @param accounts to search through
     * @param name we are searching for
     * @return the account that is owned by the person with that name or null if none exist
     */
    static private Account searchAccountsByName(List<Account> accounts, String name){
        for(Account a: accounts){
            if(a.getName().equals(name)){
                return a;
            }
        }
        return null;
    }
}
