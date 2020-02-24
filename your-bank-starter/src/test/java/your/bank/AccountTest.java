package your.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account testAccount;

    @BeforeEach
    void setup() {
        testAccount = new Account("Testy McTestface", "GBP");
    }

    @Test
    void createAccount(){
        Account a = new Account("", "GBP");
        assertNotNull(a);
    }

    @Test
    void nameAssigned() {
        assertEquals("Testy McTestface", testAccount.getName());
    }

    @Test
    void initToZero() {
        assertEquals(new BigDecimal("0.00"), testAccount.getCurrentBalance());
    }

    @Test
    void balanceAsFormattedString() {
        testAccount.deposit(new BigDecimal("14000.84"));
       // assertEquals("£14,000.84", testAccount.getBalanceAsFormattedString());
        assertTrue(Arrays.asList("£14,000.84", "GBP14,000.84").contains(testAccount.getBalanceAsFormattedString()));
    }

    @Test
    void singleIntegerDeposit() {
        testAccount.deposit(new BigDecimal(50));
        assertEquals(new BigDecimal("50.00"), testAccount.getCurrentBalance());
    }

    @Test
    void singleDecimalDeposit() {
        testAccount.deposit(new BigDecimal("12.38"));
        assertEquals(new BigDecimal("12.38"), testAccount.getCurrentBalance());
    }

    @Test
    void singleIntegerWithdrawal() {
        testAccount.deposit(new BigDecimal("100"));
        testAccount.withdraw(new BigDecimal("30"));
        assertEquals(new BigDecimal("70.00"), testAccount.getCurrentBalance());
    }

    @Test
    void singleDecimalWithdrawal() {
        testAccount.deposit(new BigDecimal("100"));
        testAccount.withdraw(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("99.99"), testAccount.getCurrentBalance());
    }

    @Test
    void multipleDepositsAndWithdrawals() {
        testAccount.deposit(new BigDecimal("12.31"));
        assertEquals(new BigDecimal("12.31"), testAccount.getCurrentBalance());

        testAccount.deposit(new BigDecimal("1200.69"));
        assertEquals(new BigDecimal("1213.00"), testAccount.getCurrentBalance());

        testAccount.deposit(new BigDecimal(900));
        assertEquals(new BigDecimal("2113.00"), testAccount.getCurrentBalance());
    }

    @Test
    void noNegativeDeposits() {
        testAccount.deposit(new BigDecimal(10));
        assertEquals(new BigDecimal("10.00"), testAccount.getCurrentBalance());

        testAccount.deposit(new BigDecimal(-5));
        assertEquals(new BigDecimal("10.00"), testAccount.getCurrentBalance());
    }

    @Test
    void noNegativeWithdrawals() {
        testAccount.deposit(new BigDecimal(10));
        assertEquals(new BigDecimal("10.00"), testAccount.getCurrentBalance());

        testAccount.withdraw(new BigDecimal(-5));
        assertEquals(new BigDecimal("10.00"), testAccount.getCurrentBalance());
    }

    @Test
    void noFractionalPennies() {
        testAccount.deposit(new BigDecimal("33.33333"));
        assertEquals(new BigDecimal("33.33"), testAccount.getCurrentBalance());
    }

    @Test
    void noOverdraft() {
        assertEquals(new BigDecimal("0.00"), testAccount.getCurrentBalance());
        testAccount.withdraw(new BigDecimal("10"));
        assertEquals(new BigDecimal("0.00"), testAccount.getCurrentBalance());
    }
}
