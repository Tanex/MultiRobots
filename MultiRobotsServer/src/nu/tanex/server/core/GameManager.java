package nu.tanex.server.core;

import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.resources.PlayerAction;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.io.ServerEngine;
import nu.tanex.server.resources.GameState;
import nu.tanex.server.resources.RegexCheck;

import java.util.function.Predicate;

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
        System.out.println("GameManager: " + client + " sent " + msg);
        if(RegexCheck.playerAction(msg)){
            client.blockActions();
            String[] splitMsg = msg.split(":");
            game.playerPerformAction(client, PlayerAction.valueOf(splitMsg[1]));
        }
        // TODO: 2015-12-19 message handling
    }
    //endregion

    //region Private methods
    private void processTurn() {
        playersTurn();
        game.checkForCollisions();
        game.handleRobotsTurn();
        game.checkForCollisions();
    }

    private void playersTurn(){
        // TODO: 2015-12-15 get player input over TCP, seems to work now?
        game.getPlayers().stream().filter(Client::isAlive).forEach(Client::requestAction);

        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        game.getPlayers().stream().filter(Client::isAwaitingAction).forEach(Client::blockActions);
    }

    public void playersLost() {
        sendMsgToAllPlayers("You lost, suck it losers");
    }

    private void sendMsgToAllPlayers(String msg){
        game.getPlayers().forEach(p -> p.sendMessage(msg));
    }

    private void sendMsgToAllPlayersMatching(String msg, Predicate<? super Client> predicate){
        game.getPlayers().stream().filter(predicate).forEach(p -> p.sendMessage(msg));
    }
    //endregion

    //region Superclass Thread
    @Override
    public void run() {
        while (gameRunning) {
            GameState gameState;
            System.out.println(game);
            do {
                processTurn();
                gameState = game.checkGameState();
                //Debug printout
                System.out.println(game);
                System.out.println("GameState: " + gameState);
            } while (gameState == GameState.Running);

            switch (gameState) {
                case PlayersWon:
                    game.nextLevel();
                    break;
                case RobotsWon:
                    playersLost();
                    gameRunning = false;
                    break;
            }
        }
        ServerEngine.getInstance().returnClientsToServer(game.getPlayers());
    }
    //endregion
}
