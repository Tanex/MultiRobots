package nu.tanex.server.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-19
 */
public class RegexCheck {
    public static boolean disconnectMsg(String msg){
        return Pattern.compile("^Disconnect$").matcher(msg).matches();
    }

    public static boolean playerAction(String msg){
        return Pattern.compile("^PlayerAction:.*$").matcher(msg).matches();
    }
}
