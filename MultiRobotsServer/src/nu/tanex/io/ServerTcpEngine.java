package nu.tanex.io;

import nu.tanex.resources.TcpEngineException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Handles keep a thread for clients to connect to that spawns a new
 * thread for each connected client to communicate via TCP.
 *
 * @author      Victor Hedlund
 * @version     0.2
 * @since       2015-11-26
 */
public class ServerTcpEngine extends Thread {
    private Vector<ClientThread> connectedClients;
    private ServerSocket serverSocket;

    /**
     * Default constructor, launches the server listening on port 2000
     * */
    public ServerTcpEngine() throws TcpEngineException {
        this(2000);
    }

    /**
     * Starts a server listening on @code port and launches a thread that
     * accepts client trying to connect to the server.
     *
     * @param   port    Port that the server should listen on
     * */
    public ServerTcpEngine(int port) throws TcpEngineException {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            throw new TcpEngineException("Error opening server socket", e);
        }
        this.start();
    }


    public void stopServerThread(){
        this.interrupt();
        try {
            connectedClients.forEach(ClientThread::interrupt);
            connectedClients.clear();

            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket while shutting down server thread");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                (new ClientThread(serverSocket.accept())).start();
            }
            catch (IOException e){
                System.out.println("Error start client thread");
                e.printStackTrace();
            }
            catch (TcpEngineException e){
                System.out.println("Error initializing client thread object");
                System.out.println((e.getCause().getMessage()));
                e.printStackTrace();
            }
        }
    }

    private class ClientThread extends Thread {
        private Socket clientSocket = null;
        private BufferedReader inStream = null;
        private PrintWriter outStream = null;

        public ClientThread(Socket clientSocket) throws TcpEngineException {
            this.clientSocket = clientSocket;
            try {
                inStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())),true);
                connectedClients.add(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new TcpEngineException("Error initializing in/out streams for client thread", e);
            }
            finally{
                //Cleanup if construction failed
                try {
                    this.clientSocket.close();
                    if (inStream != null)
                        inStream.close();
                    if (outStream != null)
                        outStream.close();
                    if (connectedClients.contains(this))
                        connectedClients.remove(this);
                }
                catch (IOException e){
                    //At this point...
                    System.out.println("Just give up...");
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            String buf;
            try {
                while (true) {
                    buf = inStream.readLine();
                    // TODO: 2015-11-26 handle received message
                }
            }
            catch (IOException e){

            }
            finally {
                try {
                    clientSocket.close();
                    connectedClients.remove(this);
                }
                catch (IOException e){
                    System.out.println("Error closing client socket");
                    e.printStackTrace();
                }
            }
        }
    }
}
