package nu.tanex.server.gui.data;

import nu.tanex.server.core.GameManager;

import java.util.ArrayList;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class GameInfo {
    private static int playersToStart = 0;

    private SettingsInfo settings;
    private int playersQueued;
    private int gameNum;
    private ArrayList<PlayerInfo> playerList;
    private boolean gameRunning;
    private int currentLevel;
    private int numRobots;
    private int numRubble;

    public static void setPlayersToStart(int playersToStart) {
        GameInfo.playersToStart = playersToStart;
    }

    public static int getPlayersToStart() {
        return playersToStart;
    }

    public int getNumPlayers(){
        return gameRunning ? playersToStart : 0;
    }

    public int getNumQueueing(){
        return gameRunning ? 0 : playersQueued;
    }

    public int getGameNum(){ return this.gameNum; }

    public GameInfo(int gameNum, GameManager gm) {
        this.gameNum = gameNum;
        settings = new SettingsInfo(gm.getGame().getSettings());
        playersQueued = gm.numPlayersQueued();
        currentLevel = gm.getGame().getLevel();
        gameRunning = gm.isGameRunning();
        numRobots = gameRunning ? gm.getGame().getNumRobots() : -1;
        numRubble = gameRunning ? gm.getGame().getNumRubble() : -1;

        playerList = new ArrayList<>();
        if (gameRunning){
            for (int i = 0; i < gm.getGame().getNumPlayers(); i++)
                playerList.add(new PlayerInfo(i, gm.getGame().getPlayers().get(i)));
        }
        else{
            for (int i = 0; i < gm.numPlayersQueued(); i++)
                playerList.add(new PlayerInfo(i, gm.getPlayerQueue().get(i)));
        }
    }

    private int getFillRate(){
        return gameRunning ? (playersToStart + numRubble + numRobots)/settings.getGridSize() : -1;
    }

    @Override
    public String toString() {
        return "Game #" + gameNum + " : " + (gameRunning ? ( "Running..." ) : ( playersQueued + "/" + playersToStart + " players queued") );
    }

    public String getInfoString(){
        return  "Game #" + gameNum + "\n" +
                "Status: " + (gameRunning ? ( "Running..." ) : ( "Waiting for more Players...") ) + "\n" +
                (gameRunning ? "Current level: " + currentLevel + "\n" : "") +
                ((getFillRate() != -1 ) ? "Board fill rate: " + getFillRate() + "\n" : "") ;
    }

    public ArrayList<PlayerInfo> getPlayerList() {
        return playerList;
    }

    public SettingsInfo getSettingsInfo() {
        return settings;
    }
}
