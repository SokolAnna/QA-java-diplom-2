import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserOrdersTest {
    UserClient userClient;
    OrderClient orderClient;
    UserRegister userRegister;
    ResponseUserData responseUserData;
    ResponseOrderData responseOrderData;
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
    @DisplayName("Get user orders with token positive result")
    @Description("Created order and find order number from user orders list")
    public void getUserOrdersWithTokenPositiveResult() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        ValidatableResponse createOrder = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int orderNumber = createOrder.extract().path("order.number");

        ValidatableResponse createResponse = orderClient.getUserOrders(responseUserData.getAccessToken());
        int statusCode = createResponse.extract().statusCode();
        assertThat("Get user orders not ok", statusCode, equalTo(SC_OK));

        boolean responseText = createResponse.extract().path("success");
        assertThat("Get user orders success is false", responseText, equalTo(true));

        responseOrderData = createResponse.extract().body().as(ResponseOrderData.class);
        List<Orders> orders = responseOrderData.getOrders();
        Orders orderActual = orders.get(0);
        assertThat("Order numbers does not match", orderActual.getNumber(), equalTo(orderNumber));
        MatcherAssert.assertThat(responseOrderData, notNullValue());
    }

    @Test
    @DisplayName("Get user orders without token unauthorized")
    @Description("Test for unauthorized user")
    public void getUserOrdersWithoutTokenUnauthorized() {
        ValidatableResponse createResponse = orderClient.getUserOrders("");
        int statusCode = createResponse.extract().statusCode();
        assertThat("Get user orders not Unauthorized", statusCode, equalTo(SC_UNAUTHORIZED));

        boolean responseText = createResponse.extract().path("success");
        assertThat("Get user orders is true", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("Get user orders message does not match", responseMessage, equalTo("You should be authorised"));
    }
}