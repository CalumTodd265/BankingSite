package your.bank.io;

import com.github.tomakehurst.wiremock.WireMockServer;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import your.bank.Account;
import your.bank.Transaction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


class DataFetcherTest {

    private WireMockServer wireMockServer = new WireMockServer();
    int port;
    private DataFetcher dataFetcher;
    private ObjectMapper objectMapper;

    private JSONParser parser = new JSONParser();

    private final String ACCOUNTS_FILENAME = "src/test/resources/TestAccounts.json";
    private final String FRAUD_FILENAME = "src/test/resources/TestFraudTransactionIDs.json";
    private final String TRANSACTIONS_FILENAME = "src/test/resources/TestTransactions.json";

    @BeforeEach
    void setUp() {
        wireMockServer.start();
        setAllStubs();
        this.port = wireMockServer.port();
        dataFetcher = new DataFetcher("http://localhost:" + this.port);
        objectMapper = new ObjectMapper();
    }

    private void setAllStubs(){

        String accountResponse = "";
        String fraudulentTransactionResponse = "";
        String transactionResponse = "";
        try {
            accountResponse = new String(Files.readAllBytes(Paths.get(ACCOUNTS_FILENAME)));
            fraudulentTransactionResponse = new String(Files.readAllBytes(Paths.get(FRAUD_FILENAME)));
            transactionResponse = new String(Files.readAllBytes(Paths.get(TRANSACTIONS_FILENAME)));
        } catch (IOException e){
            e.printStackTrace();
        }

        stubFor(
                get(
                        urlEqualTo("/accounts"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json;charset=UTF-8")
                                .withBody(accountResponse))
        );

        stubFor(
                get(
                        urlMatching("/secure/fraud(.*)")).atPriority(1)
                        .withQueryParam("token", equalTo("GfT0gVgByFekW6oHY7H1JGwH7"))
                        .withHeader("Accept", containing("json"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json;charset=UTF-8")
                                .withBody(fraudulentTransactionResponse))
        );

        stubFor(
                get(
                        urlMatching("/secure/fraud(.*)")).atPriority(2)
                        .willReturn(aResponse()
                                .withStatus(401))
        );

        stubFor(
                get(
                        urlMatching("/auth/transaction(.*)")).atPriority(1)
                        .withHeader("Authorization", containing("Basic VGVhbTU6T3lmc0dqa1BWMw=="))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json;charset=UTF-8")
                                .withBody(transactionResponse))
        );

        stubFor(
                get(
                        urlMatching("/auth/transaction(.*)")).atPriority(2)
                        .willReturn(aResponse()
                                .withStatus(401))
        );

    }

    @AfterEach
    void tearDown(){
        wireMockServer.stop();
    }

    /*****************************************************************

                                Tests

     ******************************************************************/

    @Test
    void getFraudulentTransactionIDs() {
        List<UUID> expectedIDs = getTestIDs();

        dataFetcher.setToken("GfT0gVgByFekW6oHY7H1JGwH7");
        List<UUID> receivedIDs = dataFetcher.getFraudulentTransactionIDs();
        assertEquals(3, receivedIDs.size());

        for(UUID id: receivedIDs){
            expectedIDs.contains(id);
        }

    }

    @Test
    void getFraudulentTransactionsIncorrectToken(){
        dataFetcher.setToken("GfTsdfgByFekW6oHY7H123wH7");
        List<UUID> receivedIDs = dataFetcher.getFraudulentTransactionIDs();
        assertEquals(0, receivedIDs.size());
    }

    @Test
    void getFraudulentTransactionsNoToken(){
        List<UUID> receivedIDs = dataFetcher.getFraudulentTransactionIDs();
        assertEquals(0, receivedIDs.size());
    }

    @Test
    void getTransactionsCorrectBasicAuthorizationParameters() {
        dataFetcher.setUsername("Team5");
        dataFetcher.setPassword("OyfsGjkPV3");

        List<Account> accounts = getTestAccounts();
        List<Transaction> transactions = dataFetcher.getTransactions(accounts);
        List<Transaction> expectedTransactions = getTestTransactions();

        assertEquals(expectedTransactions.size(), transactions.size());

        for(Transaction t: transactions){
            assertTrue(expectedTransactions.contains(t));
        }
    }

    @Test
    void getTransactionsIncorrectBasicAuthorizationParameters() {
        dataFetcher.setUsername("Team6");
        dataFetcher.setPassword("OyfzzzkPV3");

        List<Account> accounts = getTestAccounts();
        List<Transaction> transactions = dataFetcher.getTransactions(accounts);

        assertEquals(0, transactions.size());
    }

    @Test
    void getTransactionsNoBasicAuthorizationParameters(){
        List<Account> accounts = getTestAccounts();
        List<Transaction> transactions = dataFetcher.getTransactions(accounts);

        assertEquals(0, transactions.size());
    }

    @Test
    void getAccounts() {
        List<Account> testAccounts = getTestAccounts();

        List<Account> receivedAccounts = dataFetcher.getAccounts();

        assertEquals(3, receivedAccounts.size());
        boolean found = false;
        for(Account a: testAccounts){

            for(Account b: receivedAccounts)
                if(b.equals(a))
                    found = true;

            assertTrue(found);
            found = false;
        }

    }

    /**
     * The list of accounts we should receive (parsed) from DataFetcher
     * @return
     */
    private List<Account> getTestAccounts(){
        try {
           return objectMapper.readValue(new File(ACCOUNTS_FILENAME), new TypeReference<List<Account>>(){});
        } catch (IOException e){
            return new ArrayList<>();
        }
    }

    /**
     * The list of UUIDs we should get from datafetcher for fraudulent transactions
     * @return
     */
    private List<UUID> getTestIDs() {
        try {
            return objectMapper.readValue(new File(FRAUD_FILENAME), new TypeReference<List<UUID>>(){});
        } catch (IOException e){
            return new ArrayList<>();
        }
    }

    private List<Transaction> getTestTransactions(){
        List<Account> accounts = getTestAccounts();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(TRANSACTIONS_FILENAME));
            List<Transaction> transactions = new ArrayList<>();
            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject tran = (JSONObject) jsonArray.get(i);
                transactions.add(new Transaction(
                        tran.getAsString("id"),
                        searchAccountsByName(accounts, tran.getAsString("from")),
                        searchAccountsByName(accounts, tran.getAsString("to")),
                        new BigDecimal(tran.getAsString("amount"))));
            }
            return transactions;
        } catch (ParseException | IOException e){
            return new ArrayList<>();
        }
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
    private Account searchAccountsByName(List<Account> accounts, String name){
        for(Account a: accounts){
            if(a.getName().equals(name)){
                return a;
            }
        }
        return null;
    }

}