package nu.tanex.server.io;

import nu.tanex.engine.exceptions.TcpEngineException;
import nu.tanex.server.core.Client;
import nu.tanex.server.resources.RegexCheck;

import java.net.Socket;
import java.util.Vector;

/**
 * Singleton that handles communication between client and server, needs to get its
 * comHandler set to a valid instance of a ServerComHandler that is already running.
 *
 * @author Victor Hedlund
 * @version 0.2
 * @since 2015-11-26
 */
public class ServerComHandler {
    //region Singleton stuff
    private static ServerComHandler ourInstance = new ServerComHandler();

    public static ServerComHandler getInstance() {
        return ourInstance;
    }

    private ServerComHandler() {
        this.connectedClients = new Vector<>();
        this.tcpEngine = null;
    }
    //endregion

    //region Member variables
    private Vector<Client> connectedClients;
    private ServerTcpEngine tcpEngine;
    //endregion

    //region Get-/setters
    public void setTcpEngine(ServerTcpEngine tcpEngine){
        this.tcpEngine = tcpEngine;
    }
    //endregion

    //region Public methods
    /**
     * Handles received messages and performs the appropriate action, used for the server "lobby".
     *
     * @param client The client whose ClientThread received the message on the server
     * @param msg The message that was received
     * */
    public void msgHandler(Client client, String msg) {
        // TODO: 2015-12-19 add msg handling
        if (RegexCheck.disconnectMsg(msg))
            clientDisconnected(client);
        System.out.println(client.getPlayerNum() + " sent " + msg);
    }

    /**
     * Handles when a client connects to the server, creates a new Client object and adds it to the
     * connectedClients list.
     *
     * @param clientSocket The socket for communication with the client.
     */
    public void clientConnected(Socket clientSocket){
        try {
            Client newClient = new Client(clientSocket);
            connectedClients.add(newClient);
        } catch (TcpEngineException tcpE){
            tcpE.printStackTrace();
        }
    }

    /**
     * Handles when a client disconnects from the server.
     *
     * @param client The client that disconnected.
     */
    public void clientDisconnected(Client client){
        client.disconnect();
        connectedClients.remove(client);
    }

    //endregion
}
