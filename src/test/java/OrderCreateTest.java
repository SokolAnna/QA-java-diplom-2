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
    private int createStatusCode;
    private ArrayList<String> ingredients = new ArrayList<>();
    final String bunHash = "61c0c5a71d1f82001bdaaa6d";
    final String fillingHash = "61c0c5a71d1f82001bdaaa6f";
    final String randomHash = "61c0c5a71d1f82001bdaaaaa";
    final String randomIngredient1 = "random1";
    final String randomIngredient2 = "random2";

    private void crateRealIngredients() {
        ingredients.add(bunHash);
        ingredients.add(fillingHash);
    }

    private void createErrIngredients() {
        ingredients.add(randomIngredient1);
        ingredients.add(randomIngredient2);
    }

    private void createOneErrOneRealIngredients() {
        ingredients.add(randomHash);
        ingredients.add(fillingHash);
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        userRegister = UserGenerator.getRandom(true, true, true);

        ValidatableResponse createUser = userClient.create(userRegister);
        createStatusCode = createUser.extract().statusCode();
        responseUserData = createUser.extract().body().as(ResponseUserData.class);
    }

    @After
    public void tearDown() {
        if (createStatusCode == 200) {
            userClient.deleteUser(responseUserData.getAccessToken());
        }
        ingredients.clear();
    }

    @Test
    @DisplayName("Create order with real token")
    @Description("Positive test with two ingredients")
    public void createOrderLoginUserPositiveResult() {
        crateRealIngredients();

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not ok", statusCode, equalTo(SC_OK));

        boolean responseText = createResponse.extract().path("success");
        assertThat("Order create ok is false", responseText, equalTo(true));

        int orderNumber = createResponse.extract().path("order.number");
        assertThat("Order number is 0", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Create order with wrong ingredient hash code")
    @Description("Test with token, Internal Server Error expected")
    public void createOrderWrongIngredientServerError() {
        createErrIngredients();

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not server error", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Create order without ingredients")
    @Description("Test with token, Bad Request expected")
    public void createOrderWithoutIngredientsBadRequest() {
        ValidatableResponse createResponse = orderClient.createEmptyOrder(responseUserData.getAccessToken());
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not Bad Request", statusCode, equalTo(SC_BAD_REQUEST));

        boolean responseText = createResponse.extract().path("success");
        assertThat("Order create is true", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("Order message does not match", responseMessage, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order two ingredients without token")
    @Description("Unauthorized excepted, only authorized users can order")
    public void createOrderWithoutLoginUnauthorized() {
        crateRealIngredients();

        ValidatableResponse createResponse = orderClient.createOrder("", ingredients);
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not Unauthorized", statusCode, equalTo(SC_UNAUTHORIZED));

        boolean responseText = createResponse.extract().path("success");
        assertThat("Order create is true", responseText, equalTo(false));
    }

    @Test
    @DisplayName("Crate order random hash server error")
    @Description("The hash format like ingredient, but not real")
    public void createOrderRandomHashServerError() {
        ingredients.add(randomHash);

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not server error", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Crate order random and normal hash server error")
    @Description("The random hash format like ingredient, but not real")
    public void createOrderRandomPlusNormalHashServerError() {
        createOneErrOneRealIngredients();

        ValidatableResponse createResponse = orderClient.createOrder(responseUserData.getAccessToken(), ingredients);
        int statusCode = createResponse.extract().statusCode();
        assertThat("Order create not server error", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}