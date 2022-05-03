package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class OrderClient extends MainClient {
    private static final String ORDER_PATH = "orders";

    @Step("Create order {token}")
    public ValidatableResponse createOrder(String accessToken, ArrayList<String> ingredients) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body("{\"ingredients\": [\"" + ingredients.get(0) + "\",\"" + ingredients.get(1) + "\"]}")
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create order {token} one ingredient")
    public ValidatableResponse createOrderOneIngredient(String accessToken, ArrayList<String> ingredients) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body("{\"ingredients\": [\"" + ingredients.get(0) + "\"]}")
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create empty order {token}")
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