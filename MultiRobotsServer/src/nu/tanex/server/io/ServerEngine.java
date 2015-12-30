package nu.tanex.server.io;

import nu.tanex.core.exceptions.ServerThreadException;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.core.Client;
import nu.tanex.server.core.ServerThread;
import nu.tanex.server.resources.RegexCheck;

import java.net.Socket;


public class ServerEngine {
    //region Singleton stuff
    private static ServerEngine ourInstance = new ServerEngine();

    public static ServerEngine getInstance() {
        return ourInstance;
    }

    private ServerEngine() {
        this.connectedClients = new ClientList();
        this.tcpEngine = null;
    }
    //endregion

    //region Member variables
    private ClientList connectedClients;
    public ClientList getConnectedClients() {
        return connectedClients;
    }

    private ServerTcpEngine tcpEngine;
    private ServerThread serverThread;
    //endregion

    //region Get-/setters
    public void setTcpEngine(ServerTcpEngine tcpEngine){
        this.tcpEngine = tcpEngine;
    }
    public void setServerThread(ServerThread serverThread) {this.serverThread = serverThread; }
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
        else if(RegexCheck.queueForGame(msg)) {
            try {
                serverThread.clientQueueForGame(client, Integer.parseInt(msg.split(":")[1]));
            } catch (ServerThreadException e) {
                e.printStackTrace();
            }
        }
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

    public void returnClientsToServer(ClientList clients){
        for (Client client : clients){
            client.setMsgHandler(this::msgHandler);
            if(!connectedClients.contains(client))
                this.connectedClients.add(client);
        }
    }
    //endregion
}
