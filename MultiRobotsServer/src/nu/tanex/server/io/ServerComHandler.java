package nu.tanex.server.io;

import nu.tanex.engine.data.Player;
import nu.tanex.server.core.Client;

/**
 * Singleton that handles communication between client and server, needs to get its
 * comHandler set to a valid instance of a ServerComHandler that is already running.
 *
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-11-26
 */
public class ServerComHandler {
    //region Singleton stuff
    private static ServerComHandler ourInstance = new ServerComHandler();

    public static ServerComHandler getInstance() {
        return ourInstance;
    }

    private ServerComHandler() {
    }
    //endregion

    //region Member variables
    private IServerComEngine comEngine;
    //endregion

    //region Get-/Setters
    /**
     * Tell the handler what ComEngine to use for communication between client and server.
     *
     * @param comEngine ComEngine to use, should already be instantiated and running when passed.
     * */
    public void setComEngine(IServerComEngine comEngine) {
        this.comEngine = comEngine;
    }
    //endregion

    //region Public methods
    /**
     * Handles received messages and performs the appropriate action.
     *
     * @param sender The client whose ClientThread received the message on the server
     * @param msg The message that was received
     * */
    public void msgReceived(Client sender, String msg) {
        System.out.println(sender.getPlayerNum() + " sent " + msg);
    }

    /**
     * Creates a new client on the server that acts as the players agent in the game.
     *
     * @return The new client that was created
     * */
    public Client createNewClient() {
        return new Client();
    }

    /**
     * Sends out messages to give the specified @code player the turn.
     * <p>
     * Will send the specified @code player a message telling them it's their turn to
     * act. Will send everyone else a message to notify them that it is @code players turn.
     *
     * @param player The player whose turn it is
     * */
    public void giveTurnToPlayer(Player player) {
        for (IServerClientThread client : comEngine.getConnectedClients()) {
            if (client.getClient().equals(player))
                client.sendMsg("your turn");
            else
                client.sendMsg("player: " + player.getPlayerNum() + "turn");
        }
    }
    //endregion
}
