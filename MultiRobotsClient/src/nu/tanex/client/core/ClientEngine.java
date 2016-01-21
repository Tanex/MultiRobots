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

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class ClientEngine {
    private static final String CONNECT_INFO_FILE = "connectInfo.bin";

    private ClientTcpEngine tcpEngine;
    private IClientGuiController guiController;
    private boolean canSendActions;

    public void setGuiController(IClientGuiController guiController) {
        this.guiController = guiController;
        File fin = new File(CONNECT_INFO_FILE);
        if (fin.isFile()){
            try {
                ObjectInputStream oin = new ObjectInputStream(new FileInputStream(fin));
                ConnectScreenInfo info = (ConnectScreenInfo)oin.readObject();
                guiController.loadConnectScreenInfo(info);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        guiController.changeGuiState(GuiState.ConnectScreen);
    }

    //region Singleton stuff
    private static ClientEngine ourInstance = new ClientEngine();

    public static ClientEngine getInstance() {
        return ourInstance;
    }

    private ClientEngine() {
        this.tcpEngine = null;
    }
    //endregion

    public void setTcpEngine(ClientTcpEngine tcpEngine) {
        this.tcpEngine = tcpEngine;
    }

    public void handleMsg(String msg){
        Program.debug(msg);

        if (RegexCheck.GameState(msg))
            guiController.updateGameState(msg.split(":")[1]);
        else if(RegexCheck.GiveInput(msg)){
            canSendActions = true;
            guiController.setInputDisabled(false);
        }
        else if(RegexCheck.NoMoreInput(msg)) {
            canSendActions = false;
            guiController.setInputDisabled(true);
        }
        else if(RegexCheck.Welcome(msg)){
            guiController.changeGuiState(GuiState.LobbyScreen);
        }
        else if(RegexCheck.GamesList(msg)){
            guiController.updateGamesList(msg.split(":")[1]);
        }
        else if(RegexCheck.GameStart(msg)){
            guiController.changeGuiState(GuiState.GameScreen);
            guiController.setPlayerNum(Integer.parseInt(msg.split(":")[1]));
            guiController.setBoardWidth(Integer.parseInt(msg.split(":")[2]));
            guiController.setBoardHeight(Integer.parseInt(msg.split(":")[3]));
        }
        else if(RegexCheck.PlayerList(msg)){
            guiController.updatePlayerList(msg.split(":")[1]);
        }
        else if(RegexCheck.PlayerInfo(msg)){
            guiController.updatePlayerInfo(msg.split(":")[1]);
        }
        else if(RegexCheck.Kicked(msg)){
            guiController.changeGuiState(GuiState.LobbyScreen);
        }
    }

    public void performAction(PlayerAction playerAction) {
        if (playerAction == null || !canSendActions)
            return;

        tcpEngine.sendMsg("PlayerAction:" + playerAction);
    }

    public void queueForGame(int gameId) {
        tcpEngine.sendMsg("QueueForGame:" + gameId);
    }

    public void connectToServer(String ipAndPort) throws UnknownHostException, TcpEngineException {
        tcpEngine.connectToServer(InetAddress.getByName(ipAndPort.split(":")[0]), Integer.parseInt(ipAndPort.split(":")[1]));
    }

    public void loginToServer(String letterOne, String letterTwo, String letterThree, String IPAddress) {
        tcpEngine.sendMsg("ClientLogin:" + letterOne + letterTwo + letterThree);

        File fout = new File(CONNECT_INFO_FILE);
        try {
            ObjectOutputStream oin = new ObjectOutputStream(new FileOutputStream(fout));
            oin.writeObject(new ConnectScreenInfo(letterOne, letterTwo, letterThree, IPAddress));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exit() {
        tcpEngine.disconnectFromServer();
    }

    public void leaveGame() {
        tcpEngine.sendMsg("LeaveGame");
    }

    public void keyboardInput(KeyEvent key){
        guiController.keyPressed(key.getCode());
    }
}
