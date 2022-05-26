package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends MainClient {
    private static final String AUTH_PATH = "auth/";

    @Step("Create user {userRegister}")
    public ValidatableResponse create(UserRegister userRegister) {
        return given()
                .spec(getBaseSpec())
                .body(userRegister)
                .when()
                .post(AUTH_PATH + "register")
                .then();
    }

    @Step("Login user {userRegister}")
    public ValidatableResponse login(UserRegister userRegister) {
        return given()
                .spec(getBaseSpec())
                .body(userRegister)
                .when()
                .post(AUTH_PATH + "login")
                .then();
    }

    @Step("Update user {userRegister}")
    public ValidatableResponse updateUser(String accessToken, UserRegister userRegister) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(userRegister)
                .when()
                .patch(AUTH_PATH + "user")
                .then();
    }

    @Step("Logout user with token")
    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body("{\n" +
                        "\"token\": \"" + refreshToken + "\"\n" +
                        "}")
                .when()
                .post(AUTH_PATH + "logout")
                .then();
    }

    @Step("Delete user with token")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_PATH + "user")
                .then();
    }
}