package nu.tanex.client.io;

import nu.tanex.engine.exceptions.TcpEngineException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Handles contacting the server and establishing a TCP connection
 * and spawn a thread to listen for messages from the server.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class ClientTcpEngine extends Thread implements IClientComEngine {
    //region Member variables
    private Socket socket = null;
    private BufferedReader inStream = null;
    private PrintWriter outStream = null;
    //endregion

    //region Constructors

    /**
     * Default constructor, tries to connect to localhost on port 2000
     */
    public ClientTcpEngine() throws TcpEngineException, UnknownHostException {
        this(InetAddress.getLocalHost(), 2000);
    }

    /**
     * Tries to connect to a server att @code address : @code port and starts a
     * thread for receiving messages from the server.
     *
     * @param address InetAddress where the server can be found
     * @param port    port that the server listens on
     */
    public ClientTcpEngine(InetAddress address, int port) throws TcpEngineException {
        try {
            socket = new Socket(address, port);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            this.start();
        } catch (IOException e) {
            //Cleanup if construction failed
            try {
                this.socket.close();
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
            } catch (IOException ie) {
                //At this point...
                System.out.println("Just give up...");
                e.printStackTrace();
            }
            throw new TcpEngineException("Error setting up in/out streams for client thread", e);
        }
    }
    //endregion

    //region Superclass Thread
    @Override
    public void run() {
        ServerListener serverListener = new ServerListener();
        try {
            while (true) {
                // TODO: 2015-11-26 Add sending logic
                outStream.println();
            }
        } catch (Exception e) {

        } finally {
            try {
                serverListener.interrupt();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error sending message");
                e.printStackTrace();
            }
        }
    }
    //endregion

    //region Interface IClientComEngine
    private class ServerListener extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    inStream.readLine();
                    // TODO: 2015-11-26 Add receiving logic
                }
            } catch (IOException e) {
                System.out.println("Error receiving from server");
                e.printStackTrace();
            }
        }
    }
    //endregion
}
