package nu.tanex.core.resources;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-22
 */
public class Resources {
    //region Global settings
    /**
     * Whether to do debug printouts or not.
     */
    public static final boolean DO_DEBUG_PRINTOUT = true;
    //endregion

    //region Server settings
    /**
     * How long the HighScore list should be.
     */
    public static final int HIGH_SCORE_LIST_SIZE = 50;

    /**
     * Filename of HighScores file on disc, without extension.
     */
    public static final String HIGH_SCORE_FILE = "highScores";

    /**
     * Filename of ConnectInfo file on disc, without extension.
     */
    public static final String CONNECT_INFO_FILE = "connectInfo.bin";

    /**
     * Filename of server default settings file on disc, without extension.
     */
    public static final String SERVER_DEFAULT_CONFIG = "serverDefault";

    /**
     * Filename of game default settings file on disc, without extension.
     */
    public static final String GAME_DEFAULT_CONFIG = "gameDefault";

    /**
     * Max distance that a robot should be able to perceive a player agent.
     */
    public static final double MAX_CHASE_DISTANCE = 10;

    /**
     * How full is the game board allowed to be when created.
     */
    public static final double MAX_GRID_FILL_ALLOWED = 0.9;
    //endregion

    //region Client settings
    /**
     * Cell size to be used when drawing the grid on the client.
     */
    public static final int CELL_SIZE = 15;
    //endregion

    //region Default server settings (when no file is present)
    /**
     * Default value for how many games to run on the server.
     */
    public static final int NUM_GAMES_TO_RUN = 5;
    /**
     * Default value for what port the server should listen on.
     */
    public static final int SERVER_PORT = 2000;
    //endregion

    //region Default game settings (when no file is present)
    /**
     * Default value for the initial amount of robots.
     */
    public static final int NUM_INITIAL_ROBOTS = 10;

    /**
     * Default value for how many additional robots per level.
     */
    public static final int NUM_ADDITIONAL_ROBOTS_PER_LEVEL = 5;

    /**
     * Default value for the initial amount of rubble.
     */
    public static final int NUM_INITIAL_RUBBLE = 2;

    /**
     * Default value for robot collisions
     *
     * @see RobotCollisions
     */
    public static final RobotCollisions ROBOT_COLLISIONS = RobotCollisions.Merge;

    /**
     * Default value for the amount of safe teleports awarded per level.
     */
    public static final int NUM_SAFE_TELEPORTS_AWARDED = 1;

    /**
     * Default value for the amount of random teleports awarded per level.
     */
    public static final int NUM_RANDOM_TELEPORTS_AWARDED = 1;

    /**
     * Default value for the amount of attacks awarded per level
     */
    public static final int NUM_ATTACKS_AWARDED = 1;

    /**
     * Default value for player attack behaviour.
     *
     * @see PlayerAttacks
     */
    public static final PlayerAttacks PLAYER_ATTACKS = PlayerAttacks.KillOne;

    /**
     * Default value for robot ai mode
     *
     * @see RobotAiMode
     */
    public static final RobotAiMode ROBOT_AI_MODE = RobotAiMode.ChaseClosest;

    /**
     * Default value for grid width
     */
    public static final int GRID_WIDTH = 30;

    /**
     * Default value for grid height
     */
    public static final int GRID_HEIGHT = 20;

    /**
     * Default value for the amount of players needed to start a game.
     */
    public static final int NUM_PLAYERS_TO_START_GAME = 1;
    //endregion
}
