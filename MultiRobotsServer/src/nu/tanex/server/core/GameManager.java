package nu.tanex.server.core;

import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.resources.GameSettings;
import nu.tanex.core.resources.PlayerAction;
import nu.tanex.server.Program;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.resources.GameState;
import nu.tanex.server.resources.RegexCheck;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author      Victor Hedlund
 * @version     0.2
 * @since       2015-12-14
 */
public class GameManager {
    //region Member variables
    private Game game;
    private boolean gameRunning;
    private Thread threadInstance;
    //endregion

    //region Constructors
    public GameManager() {
        this.game = new Game();
        this.gameRunning = false;
    }
    //endregion

    public boolean isGameRunning() {
        return gameRunning;
    }

    public Game getGame() {
        return game;
    }
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
        sendMsgToAllPlayers("GameStart");
        sendPlayerList();
        game.getPlayers().stream().forEach(Client::sendPlayerInfo);
        this.gameRunning = true;
        threadInstance = new Thread(this::threadFunc);
        threadInstance.start();
    }

    /**
     * Handles received messages while a client is part of a game on the server.
     *
     * @param client Client that sent the message
     * @param msg The message that was sent.
     */
    public void msgHandler(Client client, String msg) {
        Program.debug("GameManager: " + client + " sent " + msg);
        if(RegexCheck.playerAction(msg)){
            client.blockActions();
            String[] splitMsg = msg.split(":");
            game.playerPerformAction(client, PlayerAction.valueOf(splitMsg[1]));
            game.checkForCollisions();
            sendGameState();
            sendPlayerList();
            client.sendPlayerInfo();
            if (game.getPlayers().stream().allMatch((p) -> !p.isAwaitingAction()))
                threadInstance.interrupt();
        }
        else if (RegexCheck.leaveGame(msg)){
            game.getPlayers().remove(client);
            game.getPlayers().add(new Client(client));
            ServerEngine.getInstance().returnClientToServer(client);
        }
        else if (RegexCheck.disconnectMsg(msg)){
            game.getPlayers().remove(client);
            game.getPlayers().add(new Client(client));
            ServerEngine.getInstance().clientDisconnected(client);
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
        game.getPlayers().stream().filter(Client::isAlive).forEach(Client::requestAction);
        sendPlayerList();

        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }

        game.getPlayers().stream().filter(Client::isAwaitingAction).forEach(Client::blockActions);
    }

    public void playersLost() {
        sendMsgToAllPlayers("PlayersLost");
    }

    private void sendPlayerList(){
        String str = "PlayerList:";
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Client player = game.getPlayers().get(i);
            //#<playerNum>,<playerName>,<playerStatus>@
            str += "#" + i + "," + player.getName() + ",";
            if (player.isAlive())
                str += player.isAwaitingAction() ? "Acting@" : "Done@";
            else
                str += "Dead@";
        }
        sendMsgToAllPlayers(str);
    }

    private void sendGameState(){
        sendMsgToAllPlayers("GameState:" + game);
    }

    private void sendMsgToAllPlayers(String msg){
        game.getPlayers().forEach(p -> p.sendMessage(msg));
    }

    private void sendMsgToAllPlayersMatching(String msg, Predicate<? super Client> predicate){
        game.getPlayers().stream().filter(predicate).forEach(p -> p.sendMessage(msg));
    }
    //endregion

    //region Threading
    public void threadFunc() {
        while (gameRunning) {
            GameState gameState;
            System.out.println(game);
            do {
                sendGameState();
                processTurn();
                gameState = game.checkGameState();
                //Debug printout
                Program.debug(game.toString());
                Program.debug("GameState: " + gameState);
            } while (gameState == GameState.Running);

            sendGameState();
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

    public String getGameSettings() {
        GameSettings gs = game.getSettings();
        return "\tInitial Robots - " + gs.getNumInitialRobots() + ">" +
                "\tInitial Rubble - " + gs.getNumInitialRubble() + ">" +
                "\tAdditional Robots Per Level - " + gs.getNumAdditionalRobotsPerLevel() + ">" +
                "\tRobot Collision Mode - " + gs.getRobotCollisions() + ">" +
                "\tSafe Teleports Awarded - " + gs.getNumSafeTeleportsAwarded() + ">" +
                "\tAttacks Awarded - " + gs.getNumAttacksAwarded() + ">" +
                "\tAttack Mode - " + gs.getPlayerAttacks() + ">" +
                "\tRobot AI Mode - " + gs.getRobotAiMode() + ">" +
                "\tGrid Size - " + gs.getGridWidth() + " x " + gs.getGridHeight();
    }
    //endregion
}
