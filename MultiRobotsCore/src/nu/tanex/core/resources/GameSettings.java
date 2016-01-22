package nu.tanex.core.resources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author      Victor Hedlund
 * @version     0.4
 * @since       2015-11-26
 */
public class GameSettings {
    //region Member variables
    private int numInitialRobots = Resources.NUM_INITIAL_ROBOTS;
    private int numAdditionalRobotsPerLevel = Resources.NUM_ADDITIONAL_ROBOTS_PER_LEVEL;
    private int numInitialRubble = Resources.NUM_INITIAL_RUBBLE;
    private RobotCollisions robotCollisions = Resources.ROBOT_COLLISIONS;
    private int numSafeTeleportsAwarded = Resources.NUM_SAFE_TELEPORTS_AWARDED;
    private int numRandomTeleportsAwarded = Resources.NUM_RANDOM_TELEPORTS_AWARDED;
    private int numAttacksAwarded = Resources.NUM_ATTACKS_AWARDED;
    private PlayerAttacks playerAttacks = Resources.PLAYER_ATTACKS;
    private RobotAiMode robotAiMode = Resources.ROBOT_AI_MODE;
    private int gridWidth = Resources.GRID_WIDTH;
    private int gridHeight = Resources.GRID_HEIGHT;
    private int numPlayersToStartGame = Resources.NUM_PLAYERS_TO_START_GAME;
    //endregion

    //region Get-/setters
    public int getNumPlayersToStartGame() {
        return this.numPlayersToStartGame;
    }

    public void setNumPlayersToStartGame(int numPlayersToStartGame) {
        this.numPlayersToStartGame = numPlayersToStartGame;
    }

    public int getNumInitialRobots() {
        return this.numInitialRobots;
    }

    public void setNumInitialRobots(int numInitialRobots) {
        this.numInitialRobots = numInitialRobots;
    }

    public int getNumAdditionalRobotsPerLevel() {
        return this.numAdditionalRobotsPerLevel;
    }

    public void setNumAdditionalRobotsPerLevel(int numAdditionalRobotsPerLevel) {
        this.numAdditionalRobotsPerLevel = numAdditionalRobotsPerLevel;
    }

    public int getNumInitialRubble() {
        return this.numInitialRubble;
    }

    public void setNumInitialRubble(int numInitialRubble) {
        this.numInitialRubble = numInitialRubble;
    }

    public RobotCollisions getRobotCollisions() {
        return this.robotCollisions;
    }

    public void setRobotCollisions(RobotCollisions robotCollisions) {
        this.robotCollisions = robotCollisions;
    }

    public int getNumSafeTeleportsAwarded() {
        return this.numSafeTeleportsAwarded;
    }

    public void setNumSafeTeleportsAwarded(int numSafeTeleportsAwarded) {
        this.numSafeTeleportsAwarded = numSafeTeleportsAwarded;
    }
    public int getNumRandomTeleportsAwarded() {
        return this.numRandomTeleportsAwarded;
    }

    public void setNumRandomTeleportsAwarded(int numRandomTeleportsAwarded) {
        this.numRandomTeleportsAwarded = numRandomTeleportsAwarded;
    }

    public int getNumAttacksAwarded() {
        return this.numAttacksAwarded;
    }

    public void setNumAttacksAwarded(int numAttacksAwarded) {
        this.numAttacksAwarded = numAttacksAwarded;
    }

    public PlayerAttacks getPlayerAttacks() {
        return this.playerAttacks;
    }

    public void setPlayerAttacks(PlayerAttacks playerAttacks) {
        this.playerAttacks = playerAttacks;
    }

    public RobotAiMode getRobotAiMode() {
        return this.robotAiMode;
    }

    public void setRobotAiMode(RobotAiMode robotAiMode) {
        this.robotAiMode = robotAiMode;
    }

    public int getGridWidth() {
        return this.gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
        return this.gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }
    //endregion

    //region Constructors

    /**
     * Initializes a default settings object.
     */
    public GameSettings() {
    }

    /**
     * Copy constructor.
     *
     * @param other GameSettings object to copy.
     */
    public GameSettings(GameSettings other) {
        this.numInitialRobots = other.numInitialRobots;
        this.numAdditionalRobotsPerLevel = other.numAdditionalRobotsPerLevel;
        this.numInitialRubble = other.numInitialRubble;
        this.robotCollisions = other.robotCollisions;
        this.numSafeTeleportsAwarded = other.numSafeTeleportsAwarded;
        this.numRandomTeleportsAwarded = other.numRandomTeleportsAwarded;
        this.numAttacksAwarded = other.numAttacksAwarded;
        this.playerAttacks = other.playerAttacks;
        this.robotAiMode = other.robotAiMode;
        this.gridWidth = other.gridWidth;
        this.gridHeight = other.gridHeight;
        this.numPlayersToStartGame = other.numPlayersToStartGame;
    }
    //endregion

    //region Public methods
    /**
     * Save the all settings in this object to a .ini-file.
     *
     * @param filename Filename to save to without file extension.
     */
    public void saveSettingsToFile(String filename) {
        PrintWriter fout = null;
        try {
            fout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./" + filename + ".ini"))), true);
            fout.println("#Feel free to edit this file manually");
            fout.println("#Just keep the format of <settings name>=<settings value>;");
            fout.println("#All whitespace characters are ignored");
            fout.println("#use # for comments\n");
            fout.print(this.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fout != null)
                fout.close();
        }
    }

    /**
     * Load settings from a .ini-file into this object.
     *
     * @param filename Name of file to load settings from without file extension.
     */
    public void loadSettingsFromFile(String filename) {
        try {
            //Paths.get(filename + ".ini")
            File fin = new File(filename + ".ini");
            if (!fin.isFile())
                return;
            //Read entire settings file into a string
            String file = new String(Files.readAllBytes(Paths.get(fin.getPath())));
            //Remove all linefeed and newline characters
            file = file.replaceAll("[\\r\\n]{1,2}", "");

            String[] settings = file.split(";|GameSettings");
            for (String str : settings) {
                //Skip if line is a comment
                if (!str.startsWith("#")) {
                    String[] subStrings = str.split("=");
                    String field = subStrings[0].trim();
                    String value = subStrings[1].split(";")[0].trim();
                    readSettingFromString(field, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        return "#GameSettings\n" +
                "numInitialRobots=" + numInitialRobots + ";\n" +
                "numAdditionalRobotsPerLevel=" + numAdditionalRobotsPerLevel + ";\n" +
                "numInitialRubble=" + numInitialRubble + ";\n" +
                "robotCollisions=" + robotCollisions + ";\n" +
                "numSafeTeleportsAwarded=" + numSafeTeleportsAwarded + ";\n" +
                "numRandomTeleportsAwarded=" + numRandomTeleportsAwarded + ";\n" +
                "numAttacksAwarded=" + numAttacksAwarded + ";\n" +
                "playerAttacks=" + playerAttacks + ";\n" +
                "robotAiMode=" + robotAiMode + ";\n" +
                "gridWidth=" + gridWidth + ";\n" +
                "gridHeight=" + gridHeight + ";\n" +
                "numPlayersToStartGame=" + numPlayersToStartGame + ";";
    }
    //endregion

    //region Private methods
    private void readSettingFromString(String field, String value) {
        switch (field) {
            case "numInitialRobots":
                numInitialRobots = Integer.parseInt(value);
                break;
            case "numAdditionalRobotsPerLevel":
                numAdditionalRobotsPerLevel = Integer.parseInt(value);
                break;
            case "numInitialRubble":
                numInitialRubble = Integer.parseInt(value);
                break;
            case "robotCollisions":
                robotCollisions = RobotCollisions.valueOf(value);
                break;
            case "numSafeTeleportsAwarded":
                numSafeTeleportsAwarded = Integer.parseInt(value);
                break;
            case "numRandomTeleportsAwarded":
                numRandomTeleportsAwarded = Integer.parseInt(value);
                break;
            case "numAttacksAwarded":
                numAttacksAwarded = Integer.parseInt(value);
                break;
            case "playerAttacks":
                playerAttacks = PlayerAttacks.valueOf(value);
                break;
            case "robotAiMode":
                robotAiMode = RobotAiMode.valueOf(value);
                break;
            case "gridWidth":
                gridWidth = Integer.parseInt(value);
                break;
            case "gridHeight":
                gridHeight = Integer.parseInt(value);
                break;
            case "numPlayersToStartGame=":
                numPlayersToStartGame = Integer.parseInt(value);
                break;
        }
    }
    //endregion
}
