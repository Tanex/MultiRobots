package nu.tanex.client;

import nu.tanex.engine.data.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class containing the main method.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Program {
    /**
     * Main entry point of the client program.
     * */
    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World!");
        ArrayList<Player> grid[][] = new ArrayList[100][100];
        //Player grid[][] = new Player[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                grid[i][j] = new ArrayList<Player>();
                grid[i][j].add(new Player());
                //grid[i][j] = new Player();
            }
        }
        while(true)
            ;
    }
}
