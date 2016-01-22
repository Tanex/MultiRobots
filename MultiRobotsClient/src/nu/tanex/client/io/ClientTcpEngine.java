package nu.tanex.client.io;

import nu.tanex.client.core.ClientEngine;
import nu.tanex.core.exceptions.TcpEngineException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Victor Hedlund
 * @version 0.2
 * @since 2015-12-30
 */
public class ClientTcpEngine extends Thread {

    //region Member variables
    private Socket socket = null;
    private BufferedReader inStream = null;
    private PrintWriter outStream = null;
    private boolean alive;
    //endregion

    //region Public methods
    /**
     * Starts a new thread that will send the message to the server.
     *
     * @param msg Message to send.
     */
    public synchronized void sendMsg(String msg) {
        (new Thread(() -> this.outStream.println(msg))).start();
    }

    /**
     * Connects to a server over the internet.
     *
     * @param address IPAddress of the server.
     * @param port    Port that the server is listening on.
     * @throws TcpEngineException Thrown if a connection cannot be formed.
     */
    public void connectToServer(InetAddress address, int port) throws TcpEngineException {
        try {
            socket = new Socket(address, port);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            alive = true;
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

    /**
     * Disconnects from the server.
     */
    public void disconnectFromServer() {
        outStream.println("Disconnect");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Superclass Thread
    @Override
    public void run() {
        try {
            while (alive) {
                String buf = inStream.readLine();
                ClientEngine.getInstance().handleMsg(buf);
            }
        } catch (IOException e) {
            try {
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
            } catch (IOException se) {
                e.printStackTrace();
            }
        }
    }
    //endregion
}
