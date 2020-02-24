package your.bank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import your.bank.io.DataFetcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

class BankTest {

    private final String ACCOUNTS_FILENAME = "src/test/resources/TestAccounts.json";
    private final String FRAUD_FILENAME = "src/test/resources/TestFraudTransactionIDs.json";
    private final String TRANSACTIONS_FILENAME = "src/test/resources/TestTransactions.json";

    Bank bank;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        bank = new Bank(getMockDataFetcher());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void processTransactions() {
    }

    @Test
    void searchAccountExistsForOneAccountReturnedOnName() {
        List<Account> searchResults = bank.searchAccounts("Alan");
        assertEquals(1, searchResults.size());
        assertEquals("Alan Turing", searchResults.get(0).getName());
    }

    @Test
    void searchAccountExistsForOneAccountReturnedOnBalance() {
        List<Account> searchResults = bank.searchAccounts("500");
        assertEquals(1, searchResults.size());
        assertEquals("Ada Lovelace", searchResults.get(0).getName());
    }

    @Test
    void searchAccountExistsForOneAccountReturnedOnCurrency() {
        List<Account> searchResults = bank.searchAccounts("SD");
        assertEquals(1, searchResults.size());
        assertEquals("Alonzo Church", searchResults.get(0).getName());
    }

    @Test
    void searchAccountExistsForMultipleAccountReturnedOnName() {
        List<Account> searchResults = bank.searchAccounts("Al");
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.get(0).getName().toLowerCase().contains("al"));
        assertTrue(searchResults.get(1).getName().toLowerCase().contains("al"));
    }

    @Test
    void searchAccountExistsForMultipleAccountReturnedOnBalance() {
        List<Account> searchResults = bank.searchAccounts("0");
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.get(0).getCurrentBalance().toString().toLowerCase().contains("0"));
        assertTrue(searchResults.get(1).getCurrentBalance().toString().toLowerCase().contains("0"));
    }

    @Test
    void searchAccountExistsForMultipleAccountReturnedOnCurrency() {
        List<Account> searchResults = bank.searchAccounts("U");
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.get(0).getCurrency().contains("U"));
        assertTrue(searchResults.get(1).getCurrency().contains("U"));
    }

    @Test
    void searchAccountDoesntExists() {
        List<Account> searchResults = bank.searchAccounts("X");
        assertEquals(0, searchResults.size());
        searchResults = bank.searchAccounts("4");
        assertEquals(0, searchResults.size());
        searchResults = bank.searchAccounts("5001");
        assertEquals(0, searchResults.size());
    }

    @Test
    void getAccounts() {
        List<Account> accounts = bank.getAccounts();
        assertEquals(3, accounts.size());

        for(Account a: accounts){
            assertThat(a.getName(),
                    Matchers.anyOf(
                    is("Alan Turing"),
                    is("Alonzo Church"),
                    is("Ada Lovelace")));
        }
    }

    private DataFetcher getMockDataFetcher(){
        DataFetcher dataFetcher = mock(DataFetcher.class);

        File file = new File(ACCOUNTS_FILENAME);
        List<Account> accounts;
        try {
            accounts = objectMapper.readValue(file, new TypeReference<List<Account>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            accounts = new ArrayList<>();
        }

        when(dataFetcher.getAccounts()).thenReturn(accounts);
        when(dataFetcher.getTransactions(new ArrayList<>())).thenReturn(new ArrayList<>());
        when(dataFetcher.getFraudulentTransactionIDs()).thenReturn(new ArrayList<UUID>());

        return dataFetcher;
    }

}
