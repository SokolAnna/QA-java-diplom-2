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

public class UserRegisterTest {

    UserRegister userRegister;
    UserClient userClient;
    ResponseUserData responseUserData;
    int statusCode;

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
    @DisplayName("Create unique user positive result")
    @Description("Base positive test with new user")
    public void createUniqueUserPositiveResult() {
        ValidatableResponse createResponse = userClient.create(userRegister);

        statusCode = createResponse.extract().statusCode();
        assertThat("User cannot create", statusCode, equalTo(SC_OK));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User create success is not true", responseText, equalTo(true));

        responseUserData = createResponse.extract().body().as(ResponseUserData.class);
        MatcherAssert.assertThat(responseUserData, notNullValue());
    }

    @Test
    @DisplayName("Create registered user already exists")
    @Description("Create twice same users")
    public void createRegisteredUserAlreadyExists() {
        ValidatableResponse createResponse = userClient.create(userRegister);
        statusCode = createResponse.extract().statusCode();
        responseUserData = createResponse.extract().body().as(ResponseUserData.class);

        ValidatableResponse createDoubleResponse = userClient.create(userRegister);

        int statusCode = createDoubleResponse.extract().statusCode();
        assertThat("User create is not forbidden", statusCode, equalTo(SC_FORBIDDEN));

        boolean responseText = createDoubleResponse.extract().path("success");
        assertThat("User create success is not false", responseText, equalTo(false));

        String responseMessage = createDoubleResponse.extract().path("message");
        assertThat("User create message does not match", responseMessage, equalTo("User already exists"));
    }

    @Test
    @DisplayName("Create user without name is forbidden")
    @Description("The username is one of the required parameters")
    public void createUserWithoutNameForbidden() {
        userRegister = UserGenerator.getRandom(true, true, false);
        ValidatableResponse createResponse = userClient.create(userRegister);

        int statusCode = createResponse.extract().statusCode();
        assertThat("User create is not forbidden", statusCode, equalTo(SC_FORBIDDEN));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User create success is not false", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("User create message does not match", responseMessage, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Create user without password is forbidden")
    @Description("The password is one of the required parameters")
    public void createUserWithoutPasswordForbidden() {
        userRegister = UserGenerator.getRandom(true, false, true);
        ValidatableResponse createResponse = userClient.create(userRegister);

        int statusCode = createResponse.extract().statusCode();
        assertThat("User create is not forbidden", statusCode, equalTo(SC_FORBIDDEN));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User create success is not false", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("User create message does not match", responseMessage, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Create user without email is forbidden")
    @Description("The email is one of the required parameters")
    public void createUserWithoutEmailForbidden() {
        userRegister = UserGenerator.getRandom(false, true, true);
        ValidatableResponse createResponse = userClient.create(userRegister);

        int statusCode = createResponse.extract().statusCode();
        assertThat("User create is not forbidden", statusCode, equalTo(SC_FORBIDDEN));

        boolean responseText = createResponse.extract().path("success");
        assertThat("User create success is not false", responseText, equalTo(false));

        String responseMessage = createResponse.extract().path("message");
        assertThat("User create message does not match", responseMessage, equalTo("Email, password and name are required fields"));
    }
}