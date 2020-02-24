package your.bank;

import jooby.helpers.UnirestHelper;
import org.jooby.*;
import org.jooby.hbs.Hbs;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jooby.FlashScope;
import your.bank.io.DataFetcher;


/**
 * @author jooby generator
 */
public class App extends Jooby {
    private Bank bank = new Bank(new DataFetcher("http://your-bank.herokuapp.com/api/Team5"));
    private final Logger logger = LoggerFactory.getLogger(App.class);

    {
        // -- Start Boilerplate Setup --
        use(new UnirestHelper());
        use(new Hbs());
        use(new Jackson());
        use(new Jdbc("db"));
        use(new FlashScope());

        assets("/bootstrap/**");
        assets("/footable/**");
        assets("/static/**");

        // -- End Boilerplate Setup --

        // Perform actions after startup
        onStarted(() -> {
            logger.info("Started!");
            bank.processTransactions();
        });

        // Simple GET Request
        get("/", () -> Results.html("landingpage"));

        get("/allAccounts", () ->
                Results
                        .when("text/html", () -> Results.html("bankaccounts").put("accounts", bank.getAccounts())) //change to whatever name of parsed object will be
                        .when("application/json", () -> bank.getAccounts())
                        .when("*", () -> Status.NOT_ACCEPTABLE)
        );

        get("/search", () -> Results.html("accountsearch"));

        get("/export", (req, rsp) ->  {
            Export exp = new Export(bank.getAccounts());
           rsp.download("Accounts.csv", exp.getOut());
        });

        post("/search", req -> {
            String query = req.param("name").value();
            List<Account> foundAccounts = bank.searchAccounts(query);

            if (foundAccounts.size() > 0) {
                return Results.html("/foundaccount").put("accFound", foundAccounts);
            } else {

                //req.flash("error", "Account not found");    - maybe implement later
                return Results.redirect("/search");
            }

        });



    get("/allAccounts", () ->
            Results
                    .when("text/html", () -> Results.html("bankaccounts").put("accounts", bank.getAccounts())) //change to whatever name of parsed object will be
                    .when("application/json", () -> bank.getAccounts())
                    .when("*", () -> Status.NOT_ACCEPTABLE)

    );

    }


    public static void main(final String[] args) {
        run(App::new, args);
    }

}
