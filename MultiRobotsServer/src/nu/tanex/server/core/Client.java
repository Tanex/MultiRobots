package nu.tanex.server.core;

import nu.tanex.core.data.Player;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.io.IMsgHandler;

import java.io.*;
import java.net.Socket;

/**
 * Handles a connected client, is used to keep track of a players gamestate
 * and to handle the thread responsible for communicating with the player
 * over the network.
 *
 * @author Victor Hedlund
 * @version 0.3
 * @since 2015-11-26
 */
public class Client extends Player {
    //region Member variables
    private ClientThread clientThread = null;
    private boolean awaitingAction;
    private String playerIP;
    //endregion

    //region Get-/setters
    /**
     * Get whether this Client is a dummy client used as a placeholder for a disconnected player.
     * <p>
     * This basically checks whether the Client actually has a thread running for network communication.
     *
     * @return Whether this is a dummy client or not.
     */
    public boolean isDummy() {
        return clientThread == null;
    }

    /**
     * Set what function this Client should pass along its messages to.
     *
     * @param msgHandler Function that will handle the messages.
     */
    public void setMsgHandler(IMsgHandler msgHandler) { this.clientThread.msgHandler = msgHandler; }

    /**
     * Gets whether the server has requested input from this Client and the client has yet to respond.
     *
     * @return if the server is still waiting for input from this client.
     */
    public boolean isAwaitingAction() {
        return awaitingAction;
    }

    /**
     * Set whether the server is waiting for an action to be received from this player.
     *
     * @param awaitingAction is the server waiting.
     */
    public void setAwaitingAction(boolean awaitingAction) {
        this.awaitingAction = awaitingAction;
    }

    /**
     * Get the ip address for this client.
     *
     * @return the ip address as a strnig.
     */
    public String getPlayerIP() {
        return playerIP;
    }
    //endregion

    //region Constructors
    /**
     * Initialize a client that will use the ServerEngines msgHandler.
     *
     * @see ServerEngine
     * @see IMsgHandler
     * @param clientSocket Socket that this client is connecting through.
     * @throws TcpEngineException Thrown if a thread for communicating with the client can not be started.
     */
    public Client(Socket clientSocket) throws TcpEngineException {
        this(clientSocket, ServerEngine.getInstance()::msgHandler);
    }

    /**
     * Initializes a client setting the msgHandler that should be used by this client.
     *
     * @see IMsgHandler
     * @param clientSocket Socket that this client is connecting through.
     * @param msgHandler Function that should handle incoming messages.
     * @throws TcpEngineException Thrown if a thread for communicating with the client can not be started.
     */
    public Client(Socket clientSocket, IMsgHandler msgHandler) throws TcpEngineException {
        super();
        //if (clientSocket != null)
        this.playerIP = clientSocket.getInetAddress().getHostAddress();
        this.clientThread = new ClientThread(clientSocket, msgHandler);
        awaitingAction = false;
    }

    /**
     * Creates a dummy client that can be used as a placeholder for a player that has left a game.
     *
     * @param other Client to create a dummy from.
     */
    public Client(Client other){
        this.setName(other.getName());
        this.setAlive(false);
        this.blockActions();
    }
    //endregion

    //region Public methods

    /**
     * Send the players stats to the client.
     */
    public void sendPlayerInfo() {
        sendMessage("PlayerInfo:" + this.getNumAttacks() + "," + this.getNumRandomTeleports() + "," + this.getNumSafeTeleports() + "," + this.getScore());
    }

    /**
     * Request player input from this client.
     */
    public void requestAction() {
        this.awaitingAction = true;
        this.sendMessage("GiveInput");
    }

    /**
     * Block this client from giving player input.
     */
    public void blockActions() {
        this.awaitingAction = false;
        this.sendMessage("NoMoreInput");
    }

    /**
     * Forcefully disconnect this client.
     */
    public void disconnect() {
        this.clientThread.alive = false;
    }

    /**
     * Spawns a new thread to send a message to this client.
     *
     * @param msg message to send.
     */
    public synchronized void sendMessage(String msg) {
        if (this.clientThread == null)
            return;

        (new Thread(() -> this.clientThread.outStream.println(msg))).start();
    }
    //endregion

    //region Innerclass ClientThread
    private class ClientThread extends Thread {
        //region Member variables
        private Socket clientSocket = null;
        private BufferedReader inStream = null;
        private PrintWriter outStream = null;
        private IMsgHandler msgHandler = null;
        private boolean alive = true;
        //endregion

        //region Constructors
        /**
         * Creates a new client thread object to handle communication with a client
         * and starts the thread for communication.
         *
         * @param clientSocket The socket that the client is connected to
         * */
        public ClientThread(Socket clientSocket, IMsgHandler msgHandler) throws TcpEngineException {
            this.clientSocket = clientSocket;
            this.msgHandler = msgHandler;
            try {
                //Create all the buffers needed for IO
                inStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
                this.start();
            }
            catch (IOException e) {
                e.printStackTrace();
                //Cleanup if construction failed
                try {
                    this.clientSocket.close();
                    if (inStream != null)
                        inStream.close();
                    if (outStream != null)
                        outStream.close();
                    ServerEngine.getInstance().clientDisconnected(Client.this);
                } catch (IOException ie) {
                    //At this point...
                    System.out.println("Just give up...");
                    e.printStackTrace();
                }
                throw new TcpEngineException("Error initializing in/out streams for client thread", e);
            }
        }
        //endregion

        //region Superclass Thread
        @Override
        public void run() {
            String buf;
            try {
                while (alive) {
                    buf = inStream.readLine();
                    msgHandler.handleMsg(Client.this, buf);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    clientSocket.close();
                    if (alive)
                        ServerEngine.getInstance().clientDisconnected(Client.this);
                }
                catch (IOException e) {
                    System.out.println("Error closing client socket");
                    e.printStackTrace();
                }
            }
        }
        //endregion
    }
    //endregion
}
