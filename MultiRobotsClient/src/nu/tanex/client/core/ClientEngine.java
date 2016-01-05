package nu.tanex.client.core;

import nu.tanex.client.gui.IClientGuiController;
import nu.tanex.client.io.ClientTcpEngine;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.data.Player;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;

import java.net.InetAddress;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class ClientEngine {
    private ClientTcpEngine tcpEngine;
    private IClientGuiController guiController;
    private boolean canSendActions;

    public void setGuiController(IClientGuiController guiController) { this.guiController = guiController; }

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
        try {
            tcpEngine.connectToServer(InetAddress.getLocalHost(), 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMsg(String msg){
        System.out.println(msg);

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
    }

    public void performAction(PlayerAction playerAction) {
        if (playerAction == null || !canSendActions)
            return;

        tcpEngine.sendMsg("PlayerAction:" + playerAction);
    }

    public void queueForGame(int gameId) {
        tcpEngine.sendMsg("QueueForGame:" + gameId);
    }
}
