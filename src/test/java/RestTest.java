import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class RestTest {

    public RestTest() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/IndividualAssignment_war_exploded/api/v1";

         when().get().then().statusCode(200);
    }

    @Test
    public void parties() {
        when()
                .get("/party")
                .then()
                .statusCode(200);
    }

}
