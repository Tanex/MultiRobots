package nu.tanex.client.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public abstract class RegexCheck {
    public static boolean GameState(String msg) {
        return Pattern.compile("^GameState:.*$").matcher(msg).matches();
    }

    public static boolean GiveInput(String msg) {
        return Pattern.compile("^GiveInput$").matcher(msg).matches();
    }

    public static boolean NoMoreInput(String msg) {
        return Pattern.compile("^NoMoreInput$").matcher(msg).matches();
    }
}
