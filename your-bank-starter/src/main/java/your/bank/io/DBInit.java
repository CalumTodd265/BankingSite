package your.bank.io;

import org.json.JSONArray;
import org.json.JSONObject;
import your.bank.reference.API;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to hold lots of different database io methods.
 * Is it really a handler tho?
 */
public final class DBInit {
    private DBInit() {
    }

    /**
     * Creates the accounts table if not exists.
     *
     * @param con An open database connection.
     */
    public static void createAccountsTable(Connection con) {
        try {
            Statement stmt = con.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS accounts (\n"
                    + " name VARCHAR(255) PRIMARY KEY,\n"
                    + " Balance decimal NOT NULL,\n"
                    + " Currency VARCHAR(255) NOT NULL\n);";

            stmt.execute(sql);

        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * @param con An open database connection.
     */
    public static void createTransactionsTable(Connection con) {
        try {
            Statement stmt = con.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS transactions (\n"
                    + " id VARCHAR(255) PRIMARY KEY,\n"
                    + " amount decimal NOT NULL,\n"
                    + " sender VARCHAR(255) NOT NULL\n"
                    + " receiver VARCHAR(255) NOT NULL\n);";


            stmt.execute(sql);

        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * Gets the accounts data from the API and populates the accounts table.
     *
     * @param con An open database connection.
     */
    /*public static void populateAccountsTable(Connection con) {
        try {

            JSONArray jarray = new JSONArray(DataFetcher.getHTTPResponeString(API.ROOT_URL + API.ALL_ACCOUNTS_ENDPOINT));

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jobj = jarray.getJSONObject(i);
                String sql = "INSERT INTO accounts VALUES (";
                sql += "\'" + jobj.getString("name").replaceAll("'", "''") + "', ";
                sql += jobj.getBigDecimal("amount") + ", ";
                sql += "\'" + jobj.getString("currency") + "\');";

                Statement stmt = con.createStatement();
                stmt.execute(sql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
