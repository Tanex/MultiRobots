package nu.tanex.client.core;

import javafx.scene.input.KeyEvent;
import nu.tanex.client.Program;
import nu.tanex.client.gui.IClientGuiController;
import nu.tanex.client.gui.data.ConnectScreenInfo;
import nu.tanex.client.io.ClientTcpEngine;
import nu.tanex.client.resources.GuiState;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;
import nu.tanex.core.resources.Resources;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author      Victor Hedlund
 * @version     0.3
 * @since       2015-11-26
 */
public class ClientEngine {

    //region Memeber variables
    private ClientTcpEngine tcpEngine;
    private IClientGuiController guiController;
    private boolean canSendActions;
    //endregion

    //region Singleton stuff
    private static ClientEngine ourInstance = new ClientEngine();

    public static ClientEngine getInstance() {
        return ourInstance;
    }

    private ClientEngine() {
        this.tcpEngine = null;
    }
    //endregion

    //region Setters
    /**
     * Sets the gui controller that the client should use while running.
     *
     * @see IClientGuiController
     * @param guiController Loaded fxml controller.
     */
    public void setGuiController(IClientGuiController guiController) {
        this.guiController = guiController;
        File fin = new File(Resources.CONNECT_INFO_FILE);
        if (fin.isFile()) {
            try {
                ObjectInputStream oin = new ObjectInputStream(new FileInputStream(fin));
                ConnectScreenInfo info = (ConnectScreenInfo) oin.readObject();
                guiController.loadConnectScreenInfo(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        guiController.changeGuiState(GuiState.ConnectScreen);
    }

    /**
     * Sets the tcp engine that the client should use while running
     *
     * @see ClientTcpEngine
     * @param tcpEngine tcpEngine instance.
     */
    public void setTcpEngine(ClientTcpEngine tcpEngine) {
        this.tcpEngine = tcpEngine;
    }
    //endregion

    //region Public methods
    /**
     * Handles messages received from the server.
     *
     * @param msg Message that was received.
     */
    public void handleMsg(String msg) {
        Program.debug(msg);

        if (RegexCheck.GameState(msg))
            guiController.updateGameState(msg.split(":")[1]);
        else if (RegexCheck.GiveInput(msg)) {
            canSendActions = true;
            guiController.setInputDisabled(false);
        }
        else if (RegexCheck.NoMoreInput(msg)) {
            canSendActions = false;
            guiController.setInputDisabled(true);
        }
        else if (RegexCheck.Welcome(msg)) {
            guiController.changeGuiState(GuiState.LobbyScreen);
        }
        else if (RegexCheck.GamesList(msg)) {
            guiController.updateGamesList(msg.split(":")[1]);
        }
        else if (RegexCheck.GameStart(msg)) {
            guiController.changeGuiState(GuiState.GameScreen);
            guiController.setPlayerNum(Integer.parseInt(msg.split(":")[1]));
            guiController.setBoardWidth(Integer.parseInt(msg.split(":")[2]));
            guiController.setBoardHeight(Integer.parseInt(msg.split(":")[3]));
        }
        else if (RegexCheck.PlayerList(msg)) {
            guiController.updatePlayerList(msg.split(":")[1]);
        }
        else if (RegexCheck.PlayerInfo(msg)) {
            guiController.updatePlayerInfo(msg.split(":")[1]);
        }
        else if (RegexCheck.Kicked(msg)) {
            guiController.changeGuiState(GuiState.LobbyScreen);
        }
        else if (RegexCheck.HighScoreList(msg)) {
            guiController.showHighScoreList(msg);
        }
    }

    /**
     * Tells the server what action the user has chosen to perform this turn.
     *
     * @see PlayerAction
     * @param playerAction Action to perform.
     */
    public void performAction(PlayerAction playerAction) {
        if (playerAction == null || !canSendActions)
            return;

        tcpEngine.sendMsg("PlayerAction:" + playerAction);
    }

    /**
     * Tells the server that the user wishes to queue for a game.
     *
     * @param gameId Id of game to queue for.
     */
    public void queueForGame(int gameId) {
        tcpEngine.sendMsg("QueueForGame:" + gameId);
    }

    /**
     * Connects the client to a server.
     *
     * @param ipAndPort Ip-address and port on format xxx.xxx.xxx.xxx:xxxxx
     * @throws UnknownHostException Thrown if the ip and/or port info is malformed.
     * @throws TcpEngineException   Thrown if the server can not be reached.
     */
    public void connectToServer(String ipAndPort) throws UnknownHostException, TcpEngineException {
        tcpEngine.connectToServer(InetAddress.getByName(ipAndPort.split(":")[0]), Integer.parseInt(ipAndPort.split(":")[1]));
    }

    /**
     * Logs the client in to the server with the chosen username and saves all the login info to disk.
     *
     * @param letterOne   First letter of nick.
     * @param letterTwo   Second letter of nick.
     * @param letterThree Third letter of nick.
     * @param IPAddress   Ip-address of the server that was connected to.
     */
    public void loginToServer(String letterOne, String letterTwo, String letterThree, String IPAddress) {
        tcpEngine.sendMsg("ClientLogin:" + letterOne + letterTwo + letterThree);

        File fout = new File(Resources.CONNECT_INFO_FILE);
        try {
            ObjectOutputStream oin = new ObjectOutputStream(new FileOutputStream(fout));
            oin.writeObject(new ConnectScreenInfo(letterOne, letterTwo, letterThree, IPAddress));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the program is about to close in order to properly close everything.
     */
    public void exit() {
        tcpEngine.disconnectFromServer();
    }

    /**
     * Tells the server that the client wishes to leave a game that is either queued for or currently playing.
     */
    public void leaveGame() {
        tcpEngine.sendMsg("LeaveGame");
    }

    /**
     * Handles keyboard input and sends it to the GUI-controller.
     *
     * @param key Key that was pressed.
     */
    public void keyboardInput(KeyEvent key) {
        guiController.keyPressed(key.getCode());
    }
    //endregion
}
