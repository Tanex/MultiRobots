package nu.tanex.server.io;

import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.Program;
import nu.tanex.server.core.ServerEngine;

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

    //region Public methods

    /**
     * Starts the servers listening thread listening on the given port.
     *
     * @param port port to listen on
     * @throws TcpEngineException Thrown if the socket can not be opened.
     */
    public void startServer(int port)  throws TcpEngineException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new TcpEngineException("Error opening server socket", e);
        }
        this.start();
        Program.debug("Server started on port " + port);
    }

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
                ServerEngine.getInstance().clientConnected(serverSocket.accept());
                Program.debug("Client connected");
            }
            catch (IOException e) {
                Program.debug("Error start client thread");
                e.printStackTrace();
            }
        }
    }
    //endregion
}
