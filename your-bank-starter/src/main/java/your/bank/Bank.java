package your.bank;

import your.bank.io.DataFetcher;

import java.util.*;

public class Bank {
    private Map<String, Account> accounts;
    private Map<UUID, Transaction> transactions;
    private List<UUID> fraudulentTransactions;

    private DataFetcher dataFetcher;

    public Bank(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
        dataFetcher.setUsername("Team5");
        dataFetcher.setPassword("OyfsGjkPV3");
        dataFetcher.setToken("GfT0gVgByFekW6oHY7H1JGwH7");

        accounts = new HashMap<>();
        populateAccounts();
        transactions = new HashMap<>();
        populateTransactions(new ArrayList<>(accounts.values()));

        fraudulentTransactions = dataFetcher.getFraudulentTransactionIDs();

    }

    public void processTransactions() {

        for(Transaction t: transactions.values()){
            t.approve();
        }

        revokeFraudulentTransactions();

    }

    private void revokeFraudulentTransactions(){
        for(Transaction t: transactions.values()){
            if(fraudulentTransactions.contains(t.getTransactionID())){
                t.revoke(true);
                t.getSender().incrementTransactionsFailed();
                t.getRecipient().incrementTransactionsFailed();
            }
        }
    }

    public List<Account> searchAccounts(String query) {
        List<Account> foundAcc = new ArrayList<>();
        for (Account ac : accounts.values()) {
            if (ac.getName().contains(query)
                    || ac.getCurrentBalance().toString().contains(query)
                    || ac.getInitialbalance().toString().contains(query)
                    || ac.getCurrency().contains(query)) {
                foundAcc.add(ac);
            }
        }

            return foundAcc;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts.values());
    }

    private void populateAccounts() {
        List<Account> accounts = dataFetcher.getAccounts();

        for(Account a : accounts) {
            this.accounts.put(a.getName(), a);
        }

    }

    private void populateTransactions(List<Account> accounts) {
        List<Transaction> transactions = dataFetcher.getTransactions(accounts);

        for(Transaction t : transactions) {
            this.transactions.put(t.getTransactionID(), t);
        }
    }
}
