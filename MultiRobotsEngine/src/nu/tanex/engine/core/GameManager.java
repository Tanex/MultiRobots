package nu.tanex.engine.core;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-12-14
 */
public class GameManager {
    private Game game;
    private int turn;

    public GameManager() {
        this.game = new Game();
        turn = 0;
    }

    private void processTurn(){
        if (turn == -1)
            game.moveRobots();
        else
            game.handlePlayerTurn(turn);
        game.checkGameState();
        incrementTurn();
    }

    private void incrementTurn(){
        turn++;
        if (turn == game.getNumPlayers())
            turn = -1;
    }
}
