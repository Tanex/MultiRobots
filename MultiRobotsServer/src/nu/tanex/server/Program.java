package nu.tanex.server;

import nu.tanex.engine.resources.TcpEngineException;
import nu.tanex.server.core.Client;
import nu.tanex.server.io.ServerComHandler;
import nu.tanex.server.io.ServerTcpEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Class containing the main method
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Program {
    /**
     * Main entry point of server program
     * */
    public static void main(String[] args) throws TcpEngineException, IOException {
        ServerComHandler.getInstance().setComEngine(new ServerTcpEngine());
        BufferedReader kbRdr = new BufferedReader(new InputStreamReader(System.in));
        kbRdr.readLine();
    }
}
