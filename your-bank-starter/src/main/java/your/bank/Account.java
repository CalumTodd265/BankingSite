package your.bank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;


/***
 * Represents an account of a customer
 */
public class Account {

    private BigDecimal initialbalance;
    private BigDecimal currentbalance;
    private String name;
    private NumberFormat currency;
    private int transactionsProcessed = 0;
    private int transactionsFailed = 0;
    private boolean fraudulentActivity = false;

    private final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Constructor for loading an account from the DB which already has 1
     *
     * @param name            The name of the user who owns the account
     * @param startingBalance An initial amount of money to open the account with
     */
    @JsonCreator
    public Account(@JsonProperty("name") String name,
                   @JsonProperty("amount") BigDecimal startingBalance,
                   @JsonProperty("currency") String currencyCode) {
        this(name, currencyCode);
        deposit(startingBalance);
        initialbalance = currentbalance;
    }

    /**
     * Constructor for opening a new empty account.
     *
     * @param name The name of the user who owns the account.
     */
    public Account(String name, String currencyCode) {
        this.name = name;
        this.currentbalance = new BigDecimal("0.00");
        this.currency = NumberFormat.getCurrencyInstance();
        this.currency.setCurrency(Currency.getInstance(currencyCode));
    }

    /**
     * Method for depositing money into the account.
     *
     * @param amount Immutable BigDecimal representing a positive non-zero amount of money to deposit.
     * @return True if the deposit succeeded, false if not.
     */
    public boolean deposit(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            logger.error("Attempted deposit is not a positive number.");
            return false;

        } else {
            currentbalance = currentbalance.add(amount).setScale(currency.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
            return true;
        }
    }

    /**
     * Method for withdrawing money from the account.
     *
     * @param amount Immutable BigDecimal representing a positive non-zero amount of money to withdraw.
     * @return True if the withdrawal succeeded, false if not.
     */
    public boolean withdraw(BigDecimal amount) {
        BigDecimal b = currentbalance.subtract(amount);
        BigDecimal zero = new BigDecimal(0);

        if (b.compareTo(zero) < 0) {
            logger.error("Attempted withdrawal would result in a negative balance.");

        } else if (amount.compareTo(zero) <= 0) {
            logger.error("Attemped withdrawal is not a positive number.");

        } else {
            currentbalance = b.setScale(currency.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
            return true;
        }

        return false;
    }

    /**
     * @return Immutable BigDecimal representing the current account balance.
     */
    public BigDecimal getCurrentBalance() {
        return currentbalance.setScale(2, RoundingMode.HALF_DOWN);
    }

    /**
     * @return String representation of the current balance, with currency symbol and appropriate precision.
     */
    public String getBalanceAsFormattedString() {
        logger.debug("Balance is: " + currentbalance + " and as formatted string is: " + currency.format(currentbalance) + " the currency code is: " + currency.getCurrency().getCurrencyCode());
        return currency.format(currentbalance);
    }

    /**
     * @return The name of the account holder.
     */
    public String getName() {
        return name;
    }

    /**
     * @return NumberFormat object representing the local currency in use.
     */
    public String getCurrency() {
        return currency.getCurrency().getCurrencyCode();
    }

    public BigDecimal getInitialbalance() {
        return initialbalance.setScale(2, RoundingMode.HALF_DOWN);
    }

    public int getTransactionsProcessed() {
        return transactionsProcessed;
    }

    public int getTransactionsFailed() {
        return transactionsFailed;
    }

    public boolean getFraudulentActivity() {
        return fraudulentActivity;
    }

    public void setFraudulentActivity(boolean fraudulentActivity) {
        this.fraudulentActivity = fraudulentActivity;
    }

    public void incrementTransactionsProcessed(){
        transactionsProcessed++;
    }

    public void decrementTransactionsProcessed(){
        transactionsProcessed--;
    }

    public void incrementTransactionsFailed(){
        transactionsFailed++;
    }

    /**
     * Old test method.
     *
     * @return Plain string showing account holder's name and balance.
     */
    @Override
    public String toString() {
        return name + " | " + getBalanceAsFormattedString();

    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Account){
            Account a = (Account) o;
            if(a.name.equals(this.name) &&
                    a.currentbalance.equals(this.currentbalance) &&
                    a.currency.getCurrency().getCurrencyCode().equals(this.currency.getCurrency().getCurrencyCode())){
                return true;
            }
        }

        return false;
    }

}
