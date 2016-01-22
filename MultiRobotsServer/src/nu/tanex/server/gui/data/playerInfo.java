package nu.tanex.server.gui.data;

import nu.tanex.server.core.Client;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class PlayerInfo {
    private String name;
    private int score;
    private int playerNum;
    private String IPAddress;

    public PlayerInfo(int playerNum, Client player) {
        this.playerNum = playerNum;
        this.name = player.getName();
        this.score = player.getScore();
        this.IPAddress = player.getPlayerIP();
    }

    @Override
    public String toString() {
        return name + " : " + IPAddress;
    }

    public String getInfoString(){
        return "Name: " + name + "\n" +
                "IP: " + IPAddress + "\n" +
                "Score: " + score;
    }

    public int getPlayerNum() { return playerNum; }

    public String getIPAddress() {
        return IPAddress;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerInfo))
            return false;
        return ((PlayerInfo)obj).IPAddress.equals(this.IPAddress) && ((PlayerInfo)obj).name.equals(this.name);
    }
}
