package nu.tanex.server.io;

import nu.tanex.engine.resources.TcpEngineException;
import nu.tanex.server.core.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Handles keep a thread for clients to connect to that creates a new
 * client object for each connected client to communicate via TCP.
 *
 * @author      Victor Hedlund
 * @version     0.4
 * @since       2015-11-26
 */
public class ServerTcpEngine extends Thread implements IServerComEngine {
    //region Member variables
    private Vector<IServerClientThread> connectedClients = new Vector<>();
    private ServerSocket serverSocket;
    //endregion

    //region Constructors

    /**
     * Default constructor, launches the server listening on port 2000
     */
    public ServerTcpEngine() throws TcpEngineException {
        this(2000);
    }

    /**
     * Starts a server listening on @code port and launches a thread that
     * accepts client trying to connect to the server.
     *
     * @param port Port that the server should listen on
     */
    public ServerTcpEngine(int port) throws TcpEngineException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new TcpEngineException("Error opening server socket", e);
        }
        this.start();
    }
    //endregion

    //region Public functions
    /**
     * Stops the server thread
     */
    public void stopServerThread() {
        this.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket while shutting down server thread");
            e.printStackTrace();
        }
    }
    //endregion

    //region Superclass Thread
    @Override
    public void run() {
        while (true) {
            try {
                //Block on accept and when a client connects create a new ClientThread object
                new ClientThread(serverSocket.accept());
                System.out.println("Client connected");
            }
            catch (IOException e) {
                System.out.println("Error start client thread");
                e.printStackTrace();
            }
            catch (TcpEngineException e) {
                System.out.println("Error initializing client object");
                System.out.println((e.getCause().getMessage()));
                e.printStackTrace();
            }
        }
    }
    //endregion

    //region Interface IServerComEngine
    @Override
    public Vector<IServerClientThread> getConnectedClients() {
        return connectedClients;
    }
    //endregion

    //region Innerclass ClientThread
    private class ClientThread extends Thread implements IServerClientThread {
        //region Member variables
        private Socket clientSocket = null;
        private BufferedReader inStream = null;
        private PrintWriter outStream = null;
        private Client client = null;
        //endregion

        //region Constructors
        /**
         * Creates a new client thread object to handle communication with a client
         * and starts the thread for communication.
         *
         * @param clientSocket The socket that the client is connected to
         * */
        public ClientThread(Socket clientSocket) throws TcpEngineException {
            this.clientSocket = clientSocket;
            client = ServerComHandler.getInstance().createNewClient();
            try {
                //Create all the buffers needed for IO
                inStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
                connectedClients.add(this);
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
                    connectedClients.remove(this);
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
                while (true) {
                    buf = inStream.readLine();
                    System.out.println(buf);
                    ServerComHandler.getInstance().msgReceived(client, buf);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    clientSocket.close();
                    connectedClients.remove(this);
                }
                catch (IOException e) {
                    System.out.println("Error closing client socket");
                    e.printStackTrace();
                }
            }
        }
        //endregion

        //region Interface IServerClientThread
        @Override
        public Client getClient() {
            return client;
        }

        @Override
        public void sendMsg(String msg) {
            outStream.println(msg);
        }
        //endregion
    }
    //endregion
}
