package nu.tanex.engine.resources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class GameSettings {

    //region Member variables
    private int numPlayersToStartGame = 2;
    private int numInitialRobots = 10;
    private int numAdditionalRobotsPerLevel = 5;
    private int numInitialRubble = 2;
    private RobotCollisions robotCollisions = RobotCollisions.Merge;
    private int numSafeTeleportsAwarded = 1;
    private int numAttacksAwarded = 1;
    private PlayerAttacks playerAttacks = PlayerAttacks.KillOne;
    private RobotAiMode robotAiMode = RobotAiMode.ChaseClosest;
    private int gridWidth = 30;
    private int gridHeight = 20;
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
    
    public GameSettings(){
    }

    /**
     * Save the all settings in this object to a .ini-file.
     *
     * @param filename Filename to save to without file extension.
     */
    public void saveSettingsToFile(String filename){
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
    public void loadSettingsFromFile(String filename){
        try {
            //Read entire settings file into a string
            String file = new String(Files.readAllBytes(Paths.get("./" + filename + ".ini")));
            //Remove all linefeed and newline characters
            file = file.replaceAll("[\\r\\n]{1,2}", "");

            String[] settings = file.split(";|GameSettings");
            for(String str : settings){
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

    @Override
    public String toString() {
        return "#GameSettings\n" +
                "numPlayersToStartGame=" + numPlayersToStartGame+ ";\n" +
                "numInitialRobots=" + numInitialRobots+ ";\n" +
                "numAdditionalRobotsPerLevel=" + numAdditionalRobotsPerLevel+ ";\n" +
                "numInitialRubble=" + numInitialRubble+ ";\n" +
                "robotCollisions=" + robotCollisions + ";\n" +
                "numSafeTeleportsAwarded=" + numSafeTeleportsAwarded+ ";\n" +
                "numAttacksAwarded=" + numAttacksAwarded+ ";\n" +
                "playerAttacks=" + playerAttacks + ";\n" +
                "robotAiMode=" + robotAiMode+ ";\n" +
                "gridWidth=" + gridWidth+ ";\n" +
                "gridHeight=" + gridHeight + ";";
    }

    private void readSettingFromString(String field, String value){
        switch (field){
            case "numPlayersToStartGame": numPlayersToStartGame = Integer.parseInt(value); break;
            case "numInitialRobots": numInitialRobots = Integer.parseInt(value); break;
            case "numAdditionalRobotsPerLevel": numAdditionalRobotsPerLevel = Integer.parseInt(value); break;
            case "numInitialRubble": numInitialRubble = Integer.parseInt(value); break;
            case "robotCollisions": robotCollisions = RobotCollisions.valueOf(value); break;
            case "numSafeTeleportsAwarded": numSafeTeleportsAwarded = Integer.parseInt(value); break;
            case "numAttacksAwarded": numAttacksAwarded = Integer.parseInt(value); break;
            case "playerAttacks": playerAttacks = PlayerAttacks.valueOf(value); break;
            case "robotAiMode": robotAiMode = RobotAiMode.valueOf(value); break;
            case "gridWidth": gridWidth = Integer.parseInt(value); break;
            case "gridHeight": gridHeight = Integer.parseInt(value); break;
        }
    }
}
