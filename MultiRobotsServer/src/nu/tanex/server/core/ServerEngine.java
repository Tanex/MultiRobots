package nu.tanex.server.core;

import nu.tanex.server.exceptions.ServerThreadException;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.gui.IServerGuiController;
import nu.tanex.server.io.ServerTcpEngine;
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
    private IServerGuiController guiController;
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
        System.out.println("ServerEngine: " + client + " sent " + msg);
        // TODO: 2015-12-19 add msg handling
        if (RegexCheck.disconnectMsg(msg))
            clientDisconnected(client);
        else if(RegexCheck.queueForGame(msg)) {
            try {
                serverThread.clientQueueForGame(client, Integer.parseInt(msg.split(":")[1]));
                connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
            } catch (ServerThreadException e) {
                e.printStackTrace();
            }
        }
        else if (RegexCheck.clientLogin(msg)){
            client.setName(msg.split(":")[1]);
            client.sendMessage("Welcome");
            client.sendMessage("GamesList:" + serverThread.getGamesInfo());
        }
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
            client.sendMessage("Welcome");
            client.sendMessage("GamesList:" + serverThread.getGamesInfo());
        }
        clients.clear();
        connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
    }

    public void setGuiController(IServerGuiController guiController) {
        this.guiController = guiController;
    }

    public void exit() {

    }
    //endregion
}
