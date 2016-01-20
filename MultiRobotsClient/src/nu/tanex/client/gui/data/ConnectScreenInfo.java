package nu.tanex.client.gui.data;

import java.io.Serializable;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-20
 */
public class ConnectScreenInfo implements Serializable {
    private String letterOne;
    public String getLetterOne() { return this.letterOne; }

    private String letterTwo;
    public String getLetterTwo() { return this.letterTwo; }

    private String letterThree;
    public String getLetterThree() { return this.letterThree; }

    private String IPAddress;
    public String getIPAddress() { return this.IPAddress; }

    public ConnectScreenInfo(String letterOne, String letterTwo, String letterThree, String IPAddress) {
        this.letterOne = letterOne;
        this.letterTwo = letterTwo;
        this.letterThree = letterThree;
        this.IPAddress = IPAddress;
    }
}
