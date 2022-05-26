package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient extends MainClient {
    private static final String ORDER_PATH = "orders";
    Map<String, ArrayList<String>> requestBody = new HashMap<String, ArrayList<String>>();

    @Step("Create order {token}")
    public ValidatableResponse createOrder(String accessToken, ArrayList<String> ingredients) {
        requestBody.put("ingredients", ingredients);
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create empty order {accessToken}")
    public ValidatableResponse createEmptyOrder(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body("{}")
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Get user orders with token {token}")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(ORDER_PATH)
                .then();
    }
}