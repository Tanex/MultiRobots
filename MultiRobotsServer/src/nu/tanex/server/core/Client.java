package nu.tanex.server.core;

import nu.tanex.engine.data.Player;
import nu.tanex.engine.exceptions.TcpEngineException;
import nu.tanex.server.io.IMsgHandler;
import nu.tanex.server.io.ServerComHandler;

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

    //region Get-/setters
    public void setMsgHandler(IMsgHandler msgHandler) { this.clientThread.msgHandler = msgHandler; }
    //endregion

    //region Constructors
    public Client(Socket clientSocket) throws TcpEngineException {
        this(clientSocket, ServerComHandler.getInstance()::msgHandler);
    }

    public Client(Socket clientSocket, IMsgHandler msgHandler) throws TcpEngineException {
        if (clientSocket != null)
            this.clientThread = new ClientThread(clientSocket, msgHandler);
    }
    //endregion

    public void disconnect(){
        this.clientThread.alive = false;
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
                    ServerComHandler.getInstance().clientDisconnected(Client.this);
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
                    ServerComHandler.getInstance().clientDisconnected(Client.this);
                }
                catch (IOException e) {
                    System.out.println("Error closing client socket");
                    e.printStackTrace();
                }
            }
        }
        //endregion

        //Public methods
        //public Client getClient() { return Client.this; }
        public void sendMsg(String msg) {
            outStream.println(msg);
        }
        //endregion
    }
    //endregion
}
