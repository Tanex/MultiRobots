package nu.tanex.server.core;

import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.Resources;
import nu.tanex.server.Program;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.exceptions.ServerThreadException;
import nu.tanex.server.gui.IServerGuiController;
import nu.tanex.server.gui.data.GameInfo;
import nu.tanex.server.gui.data.PlayerInfo;
import nu.tanex.server.gui.data.SettingsInfo;
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

    //region Setters

    /**
     * Set the tcp engine that the server should use for communication with the clients.
     * <p>
     * if server settings are loaded this will start the server so that it can listen for clients.
     *
     * @see ServerTcpEngine
     * @param tcpEngine tcp engine to use.
     */
    public void setTcpEngine(ServerTcpEngine tcpEngine){
        this.tcpEngine = tcpEngine;

        if (serverThread != null){
            try {
                tcpEngine.startServer(serverThread.getSettings().getServerPort());
            } catch (TcpEngineException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the server thread that the server should use for handling all running games.
     * <p>
     * If the tcp engines listen thread hasn't been started yet it is by this.
     * If the UI is loaded it is updated.
     *
     * @see ServerThread
     * @param serverThread server thread to use
     */
    public void setServerThread(ServerThread serverThread) {
        this.serverThread = serverThread;
        if (!tcpEngine.isAlive()){
            try {
                tcpEngine.startServer(serverThread.getSettings().getServerPort());
            } catch (TcpEngineException e) {
                e.printStackTrace();
            }
        }

        if (guiController != null)
            updateGui();
    }

    /**
     * Set the gui controller that the server should use.
     * <p>
     * If the ServerThread is already loaded the UI is updated.
     *
     * @see IServerGuiController
     * @param guiController controller to use
     */
    public void setGuiController(IServerGuiController guiController) {
        this.guiController = guiController;
        if (serverThread != null)
            updateGui();
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
        Program.debug("ServerEngine: " + client.getName() + " sent " + msg);
        if (RegexCheck.disconnectMsg(msg))
            clientDisconnected(client);
        else if(RegexCheck.queueForGame(msg)) {
            try {
                serverThread.clientQueueForGame(client, Integer.parseInt(msg.split(":")[1]));
                connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
            } catch (ServerThreadException e) {
                e.printStackTrace();
            }
            updateGui();
        }
        else if (RegexCheck.clientLogin(msg)){
            client.setName(msg.split(":")[1]);
            client.sendMessage("Welcome");
            client.sendMessage("GamesList:" + serverThread.getGamesInfo());
        }
        else if (RegexCheck.leaveGame(msg)) {
            serverThread.clientLeaveGame(client);
            connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
            updateGui();
        }
        else if (RegexCheck.disconnectMsg(msg)) {
            clientDisconnected(client);
            updateGui();
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
            updateGui();
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
        serverThread.clientLeaveGame(client);
        client.disconnect();
        connectedClients.remove(client);
    }

    /**
     * Returns all the client to the server (lobby) by calling returnClientToServer for every client
     *
     * @param clients clients that should be returned to the server (lobby)
     */
    public void returnClientsToServer(ClientList clients){
        clients.forEach(this::returnClientToServer);
        clients.forEach(c -> c.sendMessage(serverThread.getHighScores().toString()));
        clients.clear();
        updateGui();
    }

    /**
     * Returns all the client to the server (lobby) by redirecting their message handlers to ServerEngine and adding
     * them to the connectedClients list.
     *
     * @param client client to return to the server (lobby)
     */
    public void returnClientToServer(Client client){
        if (!client.isDummy()) {
            serverThread.getHighScores().submitScore(client);
            client.setMsgHandler(this::msgHandler);
            if (!connectedClients.contains(client) && !client.isDummy())
                this.connectedClients.add(client);
            client.sendMessage("Welcome");
            //client.sendMessage("GamesList:" + serverThread.getGamesInfo());
        }
        connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
    }

    /**
     * Called when the program is closed to properly close everything.
     */
    public void exit() {
        // TODO: 2016-01-21 add exit logic
        serverThread.getHighScores().saveToFile(Resources.HIGH_SCORE_FILE);
        serverThread.exit();
        connectedClients.forEach( c -> c.sendMessage("ServerClosing"));
    }

    /**
     * Updates all the information in the GUI.
     */
    public void updateGui() {
        guiController.updateGameList(serverThread.getGameManagers(), connectedClients.size());
    }

    /**
     * Sends settings information to the proper GameManager so that it can update its games settings.
     *
     * @param game game that should get new settings.
     * @param newSettings new settings to be applied.
     */
    public void updateGameSetting(GameInfo game, SettingsInfo newSettings) {
        serverThread.getGameManagers().get(game.getGameNum()).updateGameSettings(newSettings);
        updateGui();
        connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
    }

    /**
     * Kicks a player from the that it is playing or queueing for.
     *
     * @param player player to kick.
     * @param game game that the player is in
     */
    public void kickPlayer(PlayerInfo player, GameInfo game) {
        Program.debug("Kicking player " + player);
        serverThread.getGameManagers().get(game.getGameNum()).kickPlayer(player);
        updateGui();
        connectedClients.forEach(c -> c.sendMessage("GamesList:" + serverThread.getGamesInfo()));
    }
    //endregion
}
