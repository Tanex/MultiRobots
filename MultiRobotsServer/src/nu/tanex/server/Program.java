package nu.tanex.server;

import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.core.Game;
import nu.tanex.server.core.ServerThread;
import nu.tanex.server.io.ServerEngine;
import nu.tanex.server.io.ServerTcpEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
    public static void main(String[] args) throws TcpEngineException {
        ServerEngine.getInstance().setTcpEngine(new ServerTcpEngine());
        ServerEngine.getInstance().setServerThread(new ServerThread("serverDefault"));
        while(true)
            ;
    }

    public static void mainOld(String[] args) throws TcpEngineException, IOException {
        ServerEngine.getInstance().setTcpEngine(new ServerTcpEngine());
        //ServerEngine.getInstance().setServerThread(new ServerThread("serverDefault"));

        Game g = new Game();
        //g.addPlayers(new Client[] {new Client(null), new Client(null)});
        while(ServerEngine.getInstance().getConnectedClients().size() == 0)
            ;
        g.addPlayers(ServerEngine.getInstance().getConnectedClients());

        g.getSettings().loadSettingsFromFile("default");
        System.out.println(g.getSettings());

        try {
            g.createGameGrid();
            System.out.println(g);
        } catch (GameException e) {
            e.printStackTrace();
        }

        BufferedReader kbRdr = new BufferedReader(new InputStreamReader(System.in));
        while(!kbRdr.readLine().contains("quit")){
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            //g.handlePlayersTurn();
            g.handleRobotsTurn();
            g.checkForCollissions();
            System.out.println(g);
        }
    }
}
