package nu.tanex.client.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public abstract class RegexCheck {
    //region Patterns
    private static final Pattern GAME_STATE_PATTERN = Pattern.compile("^GameState:.*$");
    private static final Pattern GIVE_INPUT_PATTERN = Pattern.compile("^GiveInput$");
    private static final Pattern NO_MORE_INPUT_PATTERN = Pattern.compile("^NoMoreInput$");
    private static final Pattern VALID_IP_AND_PORT_PATTERN = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{1,5}$");
    private static final Pattern GAMES_LIST_PATTERN = Pattern.compile("^GamesList:.*$");
    private static final Pattern WELCOME_PATTERN = Pattern.compile("^Welcome$");
    private static final Pattern GAME_START_PATTERN = Pattern.compile("^GameStart:[0-9]+:[0-9]+:[0-9]+$");
    private static final Pattern PLAYER_LIST_PATTERN = Pattern.compile("^PlayerList:.*$");
    private static final Pattern PLAYER_INFO_PATTERN = Pattern.compile("^PlayerInfo:.*$");
    private static final Pattern KICKED_PATTERN = Pattern.compile("^Kicked$");
    private static final Pattern HIGH_SCORE_LIST_PATTERN = Pattern.compile("^(> [A-Z]{3}: [0-9]+<){0,10}$");
    //endregion

    //region Functions

    /**
     * Matches a message from the server about the game state.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean GameState(String msg) {
        return GAME_STATE_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server telling the client to give input.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean GiveInput(String msg) {
        return GIVE_INPUT_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server that the client can't give any more input.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean NoMoreInput(String msg) {
        return NO_MORE_INPUT_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a string that is a valid ip and port on the format xxx.xxx.xxx.xxx:xxxxx
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean ValidIpAndPort(String msg) {
        return VALID_IP_AND_PORT_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server about all games running on the server.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean GamesList(String msg) {
        return GAMES_LIST_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server that the client successfully connected.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean Welcome(String msg) {
        return WELCOME_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server that the game is starting.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean GameStart(String msg) {
        return GAME_START_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server with a list of all players in the game.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean PlayerList(String msg) {
        return PLAYER_LIST_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server with all the players stats in the game.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean PlayerInfo(String msg) {
        return PLAYER_INFO_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server that the client got kicked from the game.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean Kicked(String msg) {
        return KICKED_PATTERN.matcher(msg).matches();
    }

    /**
     * Matches a message from the server containing a high score list.
     * @param msg Message to match
     * @return If the message is a match or not
     */
    public static boolean HighScoreList(String msg) {
        return HIGH_SCORE_LIST_PATTERN.matcher(msg).matches();
    }
    //endregion
}
