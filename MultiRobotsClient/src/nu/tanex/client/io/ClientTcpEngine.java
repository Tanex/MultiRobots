package nu.tanex.client.io;

import nu.tanex.client.core.ClientEngine;
import nu.tanex.core.exceptions.TcpEngineException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-30
 */
public class ClientTcpEngine extends Thread {

    //region Member variables
    private Socket socket = null;
    private BufferedReader inStream = null;
    private PrintWriter outStream = null;
    //endregion

    public ClientTcpEngine() {
    }

    public synchronized void sendMsg(String msg){
        (new Thread(() -> this.outStream.println(msg))).start();
    }

    public void connectToServer(InetAddress address, int port) throws TcpEngineException {
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

    public void disconnectFromServer(){
        this.interrupt();
        try {
            this.socket.close();

            if (inStream != null)
                inStream.close();
            if (outStream != null)
                outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                ClientEngine.getInstance().handleMsg(inStream.readLine());
            }
        } catch (IOException e) {
            System.out.println("Error receiving from server");
            e.printStackTrace();
        }
    }
}
