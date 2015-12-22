package nu.tanex.server.core;

import nu.tanex.engine.exceptions.GameException;
import nu.tanex.engine.resources.PlayerAction;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.resources.GameState;
import nu.tanex.server.resources.RegexCheck;

/**
 * @author      Victor Hedlund
 * @version     0.2
 * @since       2015-12-14
 */
public class GameManager extends Thread {
    //region Member variables
    private Game game;
    private boolean gameRunning;
    //endregion

    //region Constructors
    public GameManager() {
        this.game = new Game();
        this.gameRunning = false;
    }
    //endregion

    //region Public methods

    /**
     * Starts a thread to handle playing the game with the given clients as players.
     *
     * @param players Connected clients that are the players in this game.
     * @throws GameException Thrown if the game can not be initialized due to improper GameSettings.
     */
    public void startGame(ClientList players) throws GameException {
        //Make all clients relay their messages to this GameManager instead of the server
        for (Client player : players)
            player.setMsgHandler(this::msgHandler);

        //Perform initial game setup
        this.game.addPlayers(players);
        this.game.createGameGrid();
        //Start the game
        this.gameRunning = true;
        this.start();
    }

    /**
     * Handles received messages while a client is part of a game on the server.
     *
     * @param client Client that sent the message
     * @param msg The message that was sent.
     */
    public void msgHandler(Client client, String msg) {
        if(RegexCheck.playerAction(msg)){
            String[] splitMsg = msg.split(":");
            game.playerPerformAction(client, PlayerAction.valueOf(splitMsg[1]));
//            try {
//                Client c = (Client)((new ObjectInputStream(
//                        new ByteArrayInputStream(
//                                msg.getBytes(Charset.defaultCharset())))).readObject());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        // TODO: 2015-12-19 message handling
    }
    //endregion

    //region Private methods
    private void processTurn() {
        game.handlePlayersTurn();
        game.checkForCollissions();
        game.handleRobotsTurn();
        game.checkForCollissions();
    }
    //endregion

    //region Superclass Thread
    @Override
    public void run() {
        while (gameRunning) {
            GameState gameState;
            do {
                processTurn();
                gameState = game.checkGameState();
            } while (gameState == GameState.Running);

            switch (gameState) {
                case PlayersWon:
                    game.nextLevel();
                    break;
                case RobotsWon:
                    game.playersLost();
                    gameRunning = false;
                    break;
            }
        }
        // TODO: 2015-12-19 return clients to lobby
    }
    //endregion
}
