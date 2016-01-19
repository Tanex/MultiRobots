package nu.tanex.client.core;

import nu.tanex.client.Program;
import nu.tanex.client.gui.IClientGuiController;
import nu.tanex.client.io.ClientTcpEngine;
import nu.tanex.client.resources.GuiState;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class ClientEngine {
    private ClientTcpEngine tcpEngine;
    private IClientGuiController guiController;
    private boolean canSendActions;

    public void setGuiController(IClientGuiController guiController) {
        this.guiController = guiController;
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
        }
        else if(RegexCheck.PlayerList(msg)){
            guiController.updatePlayerList(msg.split(":")[1]);
        }
        else if(RegexCheck.PlayerInfo(msg)){
            guiController.updatePlayerInfo(msg.split(":")[1]);
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

    public void loginToServer(String nick) {
        tcpEngine.sendMsg("ClientLogin:" + nick);
    }

    public void exit() {
        tcpEngine.disconnectFromServer();
    }

    public void leaveGame() {
        tcpEngine.sendMsg("LeaveGame");
    }
}
