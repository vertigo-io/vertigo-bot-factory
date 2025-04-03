package io.vertigo.chatbot.commons.utils;

import java.util.regex.Pattern;

/**
 * Utilitary class to handle Strings
 */
public class CommonsStringUtils {
    private static final Pattern emailPattern = Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\" +
            ".[_a-zA-Z0-9-]+)*(\\.[a-zA-Z0-9-]{2,3})+$");

    /**
     * Evaluate if a string has a valid email address format.
     *
     * @param emailAddress string to check
     * @return boolean.
     */
    public static boolean isValidEmailAdress(String emailAddress) {
        return emailPattern.matcher(emailAddress).matches();
    }
}
