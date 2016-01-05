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

    private ClientThread clientThread;
    private boolean awaitingAction;

    //region Get-/setters

    /**
     * Set what function this Client should pass along its messages to.
     *
     * @param msgHandler Function that will handle the messages.
     */
    public void setMsgHandler(IMsgHandler msgHandler) { this.clientThread.msgHandler = msgHandler; }

    public boolean isAwaitingAction() {
        return awaitingAction;
    }

    public void setAwaitingAction(boolean awaitingAction) {
        this.awaitingAction = awaitingAction;
    }
    //endregion

    //region Constructors
    public Client(Socket clientSocket) throws TcpEngineException {
        this(clientSocket, ServerEngine.getInstance()::msgHandler);
    }

    public Client(Socket clientSocket, IMsgHandler msgHandler) throws TcpEngineException {
        super();
        if (clientSocket != null)
            this.clientThread = new ClientThread(clientSocket, msgHandler);
        awaitingAction = false;
    }

    public void requestAction(){
        this.awaitingAction = true;
        this.sendMessage("GiveInput");
    }

    public void blockActions(){
        this.awaitingAction = false;
        this.sendMessage("NoMoreInput");
    }
    //endregion

    public void disconnect(){
        this.clientThread.alive = false;
    }

    public synchronized void sendMessage(String msg){
        (new Thread(() -> this.clientThread.outStream.println(msg))).start();
    }

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
                    System.out.println(buf);
                    msgHandler.handleMsg(Client.this, buf);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    clientSocket.close();
                    ServerEngine.getInstance().clientDisconnected(Client.this);
                }
                catch (IOException e) {
                    System.out.println("Error closing client socket");
                    e.printStackTrace();
                }
            }
        }
        //endregion


        @Override
        public String toString() {
            return getPlayerNum() + super.toString();
        }
    }
    //endregion
}
