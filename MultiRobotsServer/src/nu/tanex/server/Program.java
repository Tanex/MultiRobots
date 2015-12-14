package nu.tanex.server;

import nu.tanex.engine.core.Game;
import nu.tanex.engine.data.Player;
import nu.tanex.engine.exceptions.GameException;
import nu.tanex.engine.exceptions.TcpEngineException;
import nu.tanex.server.io.ServerComHandler;
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
    public static void main(String[] args) throws TcpEngineException, IOException {
        //ServerComHandler.getInstance().setComEngine(new ServerTcpEngine());

        Game g = new Game();
        g.addPlayers(new Player[] {new Player(), new Player()});

        g.getSettings().loadSettingsFromFile("default");
        System.out.println(g.getSettings());

        try {
            g.createGameGrid();
            System.out.println(g.getGameGrid());
        } catch (GameException e) {
            e.printStackTrace();
        }

        BufferedReader kbRdr = new BufferedReader(new InputStreamReader(System.in));
        while(!kbRdr.readLine().contains("quit")){
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            g.moveRobots();
            System.out.println(g.getGameGrid());
        }
    }
}
