package nu.tanex.server.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-19
 */
public abstract class RegexCheck {
    public static boolean disconnectMsg(String msg){
        return Pattern.compile("^Disconnect$").matcher(msg).matches();
    }

    public static boolean playerAction(String msg){
        return Pattern.compile("^PlayerAction:.*$").matcher(msg).matches();
    }

    public static boolean queueForGame(String msg) {
        return Pattern.compile("^QueueForGame:[0-9]+$").matcher(msg).matches();
    }
}
