package your.bank.io;

import your.bank.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBQueries {
    private DBQueries() {
    }

    /**
     * Parses each row of the accounts table to an Account object and returns a List of all such objects created,
     * for manipulation in memory by the application.
     *
     * @param con An open database connection.
     * @return An ArrayList of all accounts that were retrieved and parsed.
     */
    public static ArrayList<Account> getAllAccounts(Connection con) {
        ArrayList<Account> accountsFromDB = new ArrayList<>();
        try {
            String sql = "SELECT * FROM accounts;";
            Statement stmt = con.createStatement();
            ResultSet results = stmt.executeQuery(sql);

            while (results.next()) {
                Account newAccount = new Account(results.getString("name"), new BigDecimal(results.getFloat("balance")), results.getString("currency"));
                accountsFromDB.add(newAccount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountsFromDB;
    }

    public static Account findAccount(Connection con, String name) {
        Account foundAccount = new Account("QWE", new BigDecimal(0.1), "GBP");
        try {
            String sql = "SELECT * FROM accounts WHERE name = " + "'" + name + "'" + ";";
            System.out.println(sql);
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            System.out.println(result.getString("name") + result.getFloat("balance") + result.getString("currency"));

            //foundAccount = new Account(result.getString("name"), new BigDecimal(result.getFloat("balance")), result.getString("currency"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("RESULT= " + foundAccount.getName());
        return foundAccount;
    }
}
