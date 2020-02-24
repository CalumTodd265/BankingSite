package your.bank;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import jooby.test.helpers.JoobyApp;
import jooby.test.helpers.JoobyTest;
import org.jooby.Jooby;
import org.jooby.test.MockRouter;
import org.junit.jupiter.api.Test;
import org.jooby.View;

// This test requires Jooby to be running
@JoobyTest
public class AppTest {

  // This is the instance of Jooby app to run
  @JoobyApp
  protected Jooby app = new App();

  @Test
  public void integrationTestRoot() {
    /*
    Does the returned html file contain a minimum of html tags and body tags?
     */
      get("/")
              .then()
              .assertThat()
              .body(containsString("<html lang=\"en\">"))
              .body(containsString("<body>"))
              .body(containsString("</body>"))
              .body(containsString("</html>"))
              .statusCode(200)
              .contentType("text/html;charset=UTF-8");
  }

  @Test
  public void integrationTestAllAccounts() {
    /*
    Does the returned html file contain a minimum of html tags and body tags
    Also test on a random entry to ensure we are getting at least some accounts
     */
    get("/allAccounts")
            .then()
            .assertThat()
            .body(containsString("<html lang=\"en\">"))
            .body(containsString("<body>"))
            .body(containsString("</body>"))
            .body(containsString("</html>"))
            .body(containsString("Eveline O&#x27;Reilly"))
            .body(containsString("12068.03"))
            .body(containsString("BWP"))
            .statusCode(200)
            .contentType("text/html;charset=UTF-8");
  }

  @Test
  public void integrationTestSearchAccountsName() {
    /*
    Check we can search by partial name
     */
    given()
            .parameters("name", "Mark")
            .when()
            .post("/search")
            .then()
            .body(containsString("Markus Frami"))
            .body(containsString("2646.85"))
            .body(containsString("MOP"));
  }

  @Test
  public void integrationTestSearchAccountsBalance() {
    /*
    Check we can search by partial name
     */
    given()
            .parameters("name", "264")
            .when()
            .post("/search")
            .then()
            .body(containsString("Markus Frami"))
            .body(containsString("2646.85"))
            .body(containsString("MOP"));
  }

  @Test
  public void integrationTestSearchAccountsCurrency() {
    /*
    Check we can search by partial name
     */
    given()
            .parameters("name", "OP")
            .when()
            .post("/search")
            .then()
            .body(containsString("Markus Frami"))
            .body(containsString("2646.85"))
            .body(containsString("MOP"));
  }

  @Test
  public void integrationTestExport(){
    /*
    ensures the file is of type "comma seperated value"
     */
    get("/export")
            .then()
            .assertThat()
            .contentType("text/comma-separated-values;charset=UTF-8");
  }

  @Test
  public void unitTest() throws Throwable {
    View view = new MockRouter(new App())
        .get("/");

    assertEquals(view.name(), "landingpage");
  }
}
