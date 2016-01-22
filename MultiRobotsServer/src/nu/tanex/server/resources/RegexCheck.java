package nu.tanex.server.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-19
 */
public abstract class RegexCheck {
    //region Patterns
    private static final Pattern DISCONNECT_MSG_PATTERN = Pattern.compile("^Disconnect$");
    private static final Pattern PLAYER_ACTION_PATTERN = Pattern.compile("^PlayerAction:.*$");
    private static final Pattern QUEUE_FOR_GAME_PATTERN = Pattern.compile("^QueueForGame:[0-9]+$");
    private static final Pattern CLIENT_LOGIN_PATTERN = Pattern.compile("^ClientLogin:[A-Z]{3}$");
    private static final Pattern LEAVE_GAME_PATTERN = Pattern.compile("^LeaveGame$");
    //endregion

    //region Functions
    /**
     * Matches a message from the client that the client wishes to disconnect from the server.
     *
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean disconnectMsg(String msg) {
        return DISCONNECT_MSG_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the client sending an action to the server that should be performed in the game.
     *
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean playerAction(String msg) {
        return PLAYER_ACTION_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the client that it wishes to queue for a game.
     *
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean queueForGame(String msg) {
        return QUEUE_FOR_GAME_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the client that it wishes to login to the server
     *
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean clientLogin(String msg) {
        return CLIENT_LOGIN_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the client that it wishes to leave any game that it is playing or queueing for.
     *
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean leaveGame(String msg) {
        return LEAVE_GAME_PATTERN.matcher(msg).matches();
    }
    //endregion
}
