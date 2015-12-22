package nu.tanex.server.io;

import nu.tanex.engine.exceptions.TcpEngineException;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Handles keep a thread for clients to connect to that creates a new
 * client object for each connected client to communicate via TCP.
 *
 * @author      Victor Hedlund
 * @version     0.5
 * @since       2015-11-26
 */
public class ServerTcpEngine extends Thread {
    //region Member variables
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
        System.out.println("Server started");
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
                ServerComHandler.getInstance().clientConnected(serverSocket.accept());
                System.out.println("Client connected");
            }
            catch (IOException e) {
                System.out.println("Error start client thread");
                e.printStackTrace();
            }
        }
    }
    //endregion
}
