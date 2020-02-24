package your.bank.reference;

public final class API {
    private API() {
    }

    public static final String ROOT_URL = "http://your-bank.herokuapp.com/api/Team5";

    public static final String ALL_ACCOUNTS_ENDPOINT = "/accounts";

    public static final String BASIC_AUTH_ENDPOINT = "/auth";
    public static final String PARAM_AUTH_ENDPOINT = "/secure";
    public static final String HEADER_AUTH_ENDPOINT = "/protect";

    public static final String ALL_TRANSACTIONS_ENDPOINT = "/auth/transaction";
    public static final String FRAUDULENT_TRANSACTIONS_ENDPOINT = "/secure/fraud";

    //no documentation on swagger, listed as a POST endpoint?
    public static final String OVERDRAFT_ENDPOINT = "/protect/overdraft";
}

