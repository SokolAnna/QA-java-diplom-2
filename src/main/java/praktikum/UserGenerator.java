package praktikum;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public static UserRegister getRandom() {
        String userEmail = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister(userEmail, userPassword, userName);
    }

    public static UserRegister getRandomNoName() {
        String userEmail = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister(userEmail, userPassword);
    }

    public static UserRegister getRandomNoPassword() {
        String userEmail = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister(userEmail, "", userName);
    }

    public static UserRegister getRandomNoEmail() {
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister("", userPassword, userName);
    }

    public static UserRegister getRandomName() {
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister(null, null, userName);
    }

    public static UserRegister getRandomPassword() {
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        return new UserRegister(null, userPassword, null);
    }

    public static UserRegister getRandomEmail() {
        String userEmail = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        return new UserRegister(userEmail, null, null);
    }
}