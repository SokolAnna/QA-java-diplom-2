import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserUpdateDataTest {
    UserClient userClient;
    UserRegister userRegister;
    ResponseUserData responseUserData;
    private int statusCodeLogin;

    @Before
    public void setUp() {
        userClient = new UserClient();
        userRegister = UserGenerator.getRandom();
        ValidatableResponse createUser = userClient.create(userRegister);
        statusCodeLogin = createUser.extract().statusCode();
        responseUserData = createUser.extract().body().as(ResponseUserData.class);
    }

    @After
    public void tearDown() {
        if (statusCodeLogin == 200) {
            userClient.deleteUser(responseUserData.getAccessToken());
        }
    }

    @Test
    @DisplayName("Update user name positive result")
    @Description("Set only new name")
    public void updateUserNamePositiveResult() {
        UserRegister newUserData = UserGenerator.getRandomName();
        ValidatableResponse createResponse = userClient.updateUser(responseUserData.getAccessToken(), newUserData);
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        String responseName = createResponse.extract().path("user.name");

        assertThat("User cannot update data", statusCode, equalTo(SC_OK));
        assertThat("User update data success is not true", responseText, equalTo(true));
        assertThat("New user name does not match", responseName, equalTo(newUserData.getName()));
    }

    @Test
    @DisplayName("Update user email positive result")
    @Description("Set only new email")
    public void updateUserEmailPositiveResult() {
        UserRegister newUserData = UserGenerator.getRandomEmail();
        ValidatableResponse createResponse = userClient.updateUser(responseUserData.getAccessToken(), newUserData);
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        String responseEmail = createResponse.extract().path("user.email");

        assertThat("User cannot update data", statusCode, equalTo(SC_OK));
        assertThat("User update data success is not true", responseText, equalTo(true));
        assertThat("New user email does not match", responseEmail, equalTo(newUserData.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Update user password positive result")
    @Description("Set only new password")
    public void updateUserPasswordPositiveResult() {
        UserRegister newUserData = UserGenerator.getRandomPassword();
        ValidatableResponse createResponse = userClient.updateUser(responseUserData.getAccessToken(), newUserData);
        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");

        assertThat("User cannot update data", statusCode, equalTo(SC_OK));
        assertThat("User update data success is not true", responseText, equalTo(true));

        UserRegister userUpdateLogin = new UserRegister(userRegister.getEmail(), newUserData.getPassword());
        ValidatableResponse createLoginUpdate = userClient.login(userUpdateLogin);
        statusCodeLogin = createLoginUpdate.extract().statusCode();
        boolean responseTextLogin = createLoginUpdate.extract().path("success");
        responseUserData = createLoginUpdate.extract().body().as(ResponseUserData.class);

        assertThat("User cannot login with new data", statusCode, equalTo(SC_OK));
        assertThat("User update login success is not true", responseTextLogin, equalTo(true));
    }

    @Test
    @DisplayName("Update user after login positive result")
    @Description("Update all user data, including login with update data")
    public void updateUserAllDataAndLoginPositiveResult() {
        UserRegister newUserData = UserGenerator.getRandom();
        ValidatableResponse createResponse = userClient.updateUser(responseUserData.getAccessToken(), newUserData);

        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        String responseName = createResponse.extract().path("user.name");
        String responseEmail = createResponse.extract().path("user.email");

        assertThat("User cannot update data", statusCode, equalTo(SC_OK));
        assertThat("User update data success is not true", responseText, equalTo(true));
        assertThat("New user name does not match", responseName, equalTo(newUserData.getName()));
        assertThat("New user email does not match", responseEmail, equalTo(newUserData.getEmail().toLowerCase()));

        UserRegister userUpdateLogin = new UserRegister(newUserData.getEmail(), newUserData.getPassword());
        ValidatableResponse createLoginUpdate = userClient.login(userUpdateLogin);
        statusCodeLogin = createLoginUpdate.extract().statusCode();
        boolean responseTextLogin = createLoginUpdate.extract().path("success");
        responseUserData = createLoginUpdate.extract().body().as(ResponseUserData.class);

        assertThat("User cannot login with new data", statusCode, equalTo(SC_OK));
        assertThat("User update login success is not true", responseTextLogin, equalTo(true));
    }

    @Test
    @DisplayName("Update user without login unauthorized")
    @Description("Generate random user, try to update without token")
    public void updateUserWithoutLoginUnauthorized() {
        statusCodeLogin = 0;
        ValidatableResponse createResponse = userClient.updateUser("", userRegister);

        int statusCode = createResponse.extract().statusCode();
        boolean responseText = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");

        assertThat("User can update data without login", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("User update data is not false", responseText, equalTo(false));
        assertThat("User update message does not match", responseMessage, equalTo("You should be authorised"));
    }
}