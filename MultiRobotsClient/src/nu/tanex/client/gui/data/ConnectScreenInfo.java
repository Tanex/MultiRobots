package nu.tanex.client.gui.data;

import java.io.Serializable;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-20
 */
public class ConnectScreenInfo implements Serializable {
    //region Member variables
    private String letterOne;
    private String letterTwo;
    private String letterThree;
    private String IPAddress;
    //endregion

    //region Getters
    /**
     * Gets the first letter of the users chosen arcade-style nickname
     *
     * @return first letter of nickname
    */
    public String getLetterTwo() {
        return this.letterTwo;
    }

    /**
     * Gets the second letter of the users chosen arcade-style nickname
     *
     * @return second letter of nickname
     */
    public String getLetterOne() {
        return this.letterOne;
    }

    /**
     * Gets the third letter of the users chosen arcade-style nickname
     *
     * @return third letter of nickname
     */
    public String getLetterThree() {
        return this.letterThree;
    }

    /**
     * Gets the IP address that the user wishes to connect to.
     *
     * @return server IP address
     */
    public String getIPAddress() {
        return this.IPAddress;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the object with all information so that it is ready to be serialized and saved to disk.
     *
     * @param letterOne first letter of nickname
     * @param letterTwo second letter of nickname
     * @param letterThree third letter of nickname
     * @param IPAddress server IP address
     */
    public ConnectScreenInfo(String letterOne, String letterTwo, String letterThree, String IPAddress) {
        this.letterOne = letterOne;
        this.letterTwo = letterTwo;
        this.letterThree = letterThree;
        this.IPAddress = IPAddress;
    }
    //endregion
}
