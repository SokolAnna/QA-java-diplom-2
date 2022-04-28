import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.*;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderCreateTest {
    UserClient userClient;
    OrderClient orderClient;
    UserRegister userRegister;
    ResponseUserData responseUserData;
    ArrayList<String> ingredients = new ArrayList<>();
    private int createStatusCode;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        userRegister = UserGenerator.getRandom();
        ValidatableResponse createUser = userClient.create(userRegister);
        createStatusCode = createUser.extract().statusCode();
        responseUserData = createUser.extract().body().as(ResponseUserData.class);
    }

    @After
    public void tearDown() {
        if (createStatusCode == 200) {
            userClient.deleteUser(responseUserData.getAccessToken());
        }
    }

    @Test
    @DisplayName("Create order with real token")
    @Description("Positive test with two ingredients")
    public void createOrderLoginUserPositiveResult() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        int orderNumber = createResponse.extract().path("order.number");

        assertThat("Order create not ok", statusCode, equalTo(SC_OK));
        assertThat("Order create ok is false", responseText, equalTo(true));
        assertThat("Order number is 0", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Create order with wrong ingredient hash code")
    @Description("Test with token, Internal Server Error expected")
    public void createOrderWrongIngredientServerError() {
        ingredients.add("random1");
        ingredients.add("random2");

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();

        assertThat("Order create not ok", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Create order without ingredients")
    @Description("Test with token, Bad Request expected")
    public void createOrderWithoutIngredientsBadRequest() {
        ValidatableResponse createResponse = orderClient.createEmptyOrder(responseUserData.getAccessToken());
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");

        assertThat("Order create not Bad Request", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Order create is true", responseText, equalTo(false));
        assertThat("Order message does not match", responseMessage, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order two ingredients without token")
    @Description("Unauthorized excepted, only authorized users can order")
    public void createOrderWithoutLoginUnauthorized() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");

        ValidatableResponse createResponse = orderClient.createOrder("", ingredients);
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");

        assertThat("Order create not Unauthorized", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Order create is true", responseText, equalTo(false));
    }
}