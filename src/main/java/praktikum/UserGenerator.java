package praktikum;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public static UserRegister getRandom(boolean withEmail, boolean withPassword, boolean withName) {
        String userEmail = null;
        String userPassword = null;
        String userName = null;
        if (withEmail) {
            userEmail = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        }
        if (withPassword) {
            userPassword = RandomStringUtils.randomAlphabetic(10);
        }
        if (withName) {
            userName = RandomStringUtils.randomAlphabetic(10);
        }
        return new UserRegister(userEmail, userPassword, userName);
    }
}