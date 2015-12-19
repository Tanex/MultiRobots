package nu.tanex.server.core;

import nu.tanex.engine.exceptions.GameException;

/**
 * @author      Victor Hedlund
 * @version     0.2
 * @since       2015-12-14
 */
public class GameManager extends Thread {
    private Game game;
    private int turn;
    private boolean gameRunning;

    public GameManager() {
        this.game = new Game();
        this.gameRunning = false;
        turn = 0;
    }

    public void startGame(Client[] players) throws GameException {
        for(Client player : players)
            player.setMsgHandler(this::msgHandler);

        this.game.addPlayers(players);
        this.game.createGameGrid();
        this.gameRunning = true;
        this.start();
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

    public void msgHandler(Client client, String msg){
        // TODO: 2015-12-19 message handling
    }

    @Override
    public void run() {
        while(gameRunning){
            processTurn();
        }
    }
}
