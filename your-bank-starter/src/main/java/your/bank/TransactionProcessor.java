package your.bank;

import java.util.*;

public class TransactionProcessor {
    private Map<UUID, Transaction> pendingTransactions;

    public TransactionProcessor() {
        pendingTransactions = new HashMap<>();
    }

    /**
     * Convert a transaction json string to a Transaction object and store it in the ArrayList
     *
     * @param transaction
     */
    public void queueTransaction(Transaction transaction) {
        pendingTransactions.put(transaction.getTransactionID(), transaction);
    }

    /**
     *
     * @param transactionIDs
     */
    public void markAllFraudulent(List<UUID> transactionIDs) {
        for(UUID id : transactionIDs) {
            pendingTransactions.get(id).revoke(true);
            pendingTransactions.remove(id);
        }
    }

    /**
     *
     */
    public void applyAll() {
        for(Transaction t : pendingTransactions.values()) {
            t.approve();
        }
    }

}
