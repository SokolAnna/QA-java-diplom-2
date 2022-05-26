import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    UserClient userClient;
    UserRegister userRegister;
    ResponseUserData responseUserData;
    private int statusCode;

    @Before
    public void setUp() {
        userClient = new UserClient();
        userRegister = UserGenerator.getRandom(true, true, true);
    }

    @After
    public void tearDown() {
        if (statusCode == 200) {
            userClient.deleteUser(responseUserData.getAccessToken());
        }
    }

    @Test
    @DisplayName("Login with registered user positive result")
    @Description("Basic positive test for user login")
    public void loginRegisterUserPositiveResult() {
        ValidatableResponse createUser = userClient.create(userRegister);
        UserRegister userLogin = new UserRegister(userRegister.getEmail(), userRegister.getPassword());
        ValidatableResponse createResponse = userClient.login(userLogin);

        statusCode = createResponse.extract().statusCode();
        assertThat("User cannot login", statusCode, equalTo(SC_OK));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User login success is not true", responseText, equalTo(true));

        responseUserData = createResponse.extract().body().as(ResponseUserData.class);
        MatcherAssert.assertThat(responseUserData, notNullValue());
    }

    @Test
    @DisplayName("Login with incorrect login and password unauthorized")
    @Description("Created random unregistered user")
    public void loginIncorrectLoginAndPasswordUnauthorized() {
        UserRegister userLogin = new UserRegister(userRegister.getEmail(), userRegister.getPassword());
        ValidatableResponse createResponse = userClient.login(userLogin);

        statusCode = createResponse.extract().statusCode();
        assertThat("User can login with wrong password", statusCode, equalTo(SC_UNAUTHORIZED));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User login success is not false", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("User login message does not match", responseMessage, equalTo("email or password are incorrect"));
    }
}