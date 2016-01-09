package nu.tanex.client.data;

import com.sun.org.apache.xpath.internal.operations.Equals;
import nu.tanex.core.resources.GameSettings;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-07
 */
public class GameInfo {
    private int playersQueued;
    private int playersNeeded;
    private String gameSettings;
    private int gameId;

    public GameInfo(String gameInfo){
        //gameId,clientsQueued,clientsNeeded,gameSettings@
        String str[] = gameInfo.split(",");
        gameId = Integer.parseInt(str[0]);
        playersQueued = Integer.parseInt(str[1]);
        playersNeeded = Integer.parseInt(str[2]);
        gameSettings = str[3].replace(">", "\n");
    }

    @Override
    public String toString() {
        return "Game #" + gameId + ". " + playersQueued + "/" + playersNeeded + " players.";
    }

    public String getGameInfoString() {
        return "Game #" + gameId + "\n" +
                "Players queued: " + playersQueued + "/" + playersNeeded + "\n" +
                "Settings:\n" + gameSettings;
    }

    public int getGameId() {
        return gameId;
    }

    public int getPlayersQueued() {
        return playersQueued;
    }

    public int getPlayersNeeded() {
        return playersNeeded;
    }

    public void updateInfo(String gameInfo){
        //gameId,clientsQueued,clientsNeeded,gameSettings@
        String str[] = gameInfo.split(",");
        playersQueued = Integer.parseInt(str[1]);
        playersNeeded = Integer.parseInt(str[2]);
        gameSettings = str[3].replace(">", "\n");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameInfo))
            return false;
        return ((GameInfo)obj).gameId == this.gameId;
    }
}
