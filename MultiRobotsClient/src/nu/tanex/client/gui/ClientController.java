package nu.tanex.client.gui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nu.tanex.client.Program;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.client.gui.data.ConnectScreenInfo;
import nu.tanex.client.gui.data.GameInfo;
import nu.tanex.client.resources.GuiState;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;
import nu.tanex.core.resources.Resources;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * @author Victor Hedlund
 * @version 0.5
 * @since 2016-01-05
 */
public class ClientController implements IClientGuiController, Initializable {
    //region Member variables
    private int playerNum = -1;
    private int playerX = -1;
    private int playerY = -1;
    private int xOffset = 0;
    private int yOffset = 0;
    private HashMap<Integer, Color> playerColors = new HashMap<>();
    private FadeTransition levelAnnounce = null;
    private DoubleProperty inputTime = null;
    private Timeline inputTimeline = null;
    private String highScoreList = "";
    //endregion

    //region GUI controls
    public Label deathLabel;
    public Label levelLabel;
    public Label giveInputLabel;
    public Canvas playerListCanvas;
    public Button buttonMoveUpLeft;
    public Button buttonMoveUp;
    public Button buttonMoveUpRight;
    public Button buttonMoveLeft;
    public Button buttonWait;
    public Button buttonMoveRight;
    public Button buttonMoveDownLeft;
    public Button buttonMoveDown;
    public Button buttonMoveDownRight;
    public Button buttonAttack;
    public Button buttonTeleport;
    public Button buttonSafeTeleport;
    public SplitPane playerControls;
    public TextField ipText;
    public Label nick1;
    public Label nick2;
    public Label nick3;
    public Button nick1Up;
    public Button nick2Up;
    public Button nick3Up;
    public Button nick1Down;
    public Button nick2Down;
    public Button nick3Down;
    public Group GameGroup;
    public Group LobbyGroup;
    public Group ConnectGroup;
    public ListView<GameInfo> gamesList;
    public Label gameInfoLabel;
    public Label waitingForPlayersLabel;
    public Button queueForGame;
    public Button leaveQueue;
    public Label playerInfoLabel;
    public Canvas gameCanvas;
    public Button connectButton;
    //endregion

    //region Interface Initializable
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        levelAnnounce = new FadeTransition(Duration.millis(5000), levelLabel);
        levelLabel.setOpacity(0.0);
        levelAnnounce.setFromValue(1.0);
        levelAnnounce.setToValue(0.1);

        inputTime = new SimpleDoubleProperty();
        giveInputLabel.textProperty().bind(inputTime.asString("Perform Your Action: %.2f"));
        giveInputLabel.setDisable(true);
    }
    //endregion

    //region Interface IClientGuiController
    /**
     * Tell the user that they were kicked from the game and returns them to the lobby
     */
    @Override
    public void kickedFromGame() {
        changeGuiState(GuiState.LobbyScreen);
        Platform.runLater(() -> {
            Alert kickedAlert = new Alert(Alert.AlertType.WARNING);
            kickedAlert.setTitle("Kicked");
            kickedAlert.setContentText("You were kicked from the game by the server admin.");
            kickedAlert.getButtonTypes().setAll(ButtonType.OK);
            kickedAlert.show();
        });
    }

    /**
     * Tells the client that the server closed and returns them to the connect screen.
     */
    @Override
    public void serverClosed() {
        changeGuiState(GuiState.ConnectScreen);
        Platform.runLater(() -> {
            Alert serverClosedAlert = new Alert(Alert.AlertType.WARNING);
            serverClosedAlert.setTitle("Server Closed");
            serverClosedAlert.setContentText("The server closed.");
            serverClosedAlert.getButtonTypes().setAll(ButtonType.OK);
            serverClosedAlert.show();
        });
    }

    /**
     * Tells the player that the game is over and that the robots won the game.
     */
    @Override
    public void gameOver() {
        Platform.runLater(() -> {
            Alert gameOverAlert = new Alert(Alert.AlertType.WARNING);
            gameOverAlert.setTitle("Game Over");
            gameOverAlert.setHeaderText("The players lost the game...");
            gameOverAlert.setContentText("Now the robot overlords have taken over the world.\n" +
                                         "The human race is no more...\n\n\n" +
                                         "Would you like to view the high score list to take\n" +
                                         "your mind of the tragedy that you brought onto mankind?");
            gameOverAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            gameOverAlert.showAndWait();

            if (gameOverAlert.getResult() == ButtonType.YES) {
                Alert highScoreAlert = new Alert(Alert.AlertType.NONE);
                highScoreAlert.setTitle("High scores");
                highScoreAlert.setHeaderText("Top scores of all time:");
                highScoreAlert.setContentText(highScoreList);
                highScoreAlert.getButtonTypes().setAll(ButtonType.OK);
                highScoreAlert.show();
            }
        });
    }

    /**
     * Tell the player that they died.
     */
    @Override
    public void playerDied() {
        Platform.runLater(() -> deathLabel.setVisible(true));
    }

    /**
     * Announces to the user what level the game is on.
     *
     * @param level level number.
     */
    @Override
    public void newLevel(int level) {
        Platform.runLater(() -> {
            levelLabel.setText("LEVEL" + (level + 1));
            levelAnnounce.play();
        });
    }

    /**
     * Tells the gui what height the game board has so that it can be used to center it on screen.
     *
     * @param height Number of squares high.
     */
    @Override
    public void setBoardHeight(int height) {
        yOffset = (int)gameCanvas.getHeight() / 2 - height * Resources.CELL_SIZE / 2;
    }

    /**
     * Tells the gui what width the game board has so that it can be used to center it on screen.
     *
     * @param width Number of squares wide.
     */
    @Override
    public void setBoardWidth(int width) {
        xOffset = (int)gameCanvas.getWidth() / 2 - width * Resources.CELL_SIZE / 2;
    }

    /**
     * Updates the game board to display the current game state.
     *
     * @param gameState New game state.
     */
    @Override
    public void updateGameState(String gameState) {
        Platform.runLater(() -> {
            drawGameBoard(gameCanvas.getGraphicsContext2D(), gameState);
        });
    }

    /**
     * Controls whether the player can give input or not.
     *
     * @param disabled Is the input disabled.
     */
    @Override
    public void setInputDisabled(boolean disabled) {
        Platform.runLater(() -> {
            playerControls.setDisable(disabled);

            if(disabled){
                giveInputLabel.setVisible(false);
                if (inputTimeline != null)
                    inputTimeline.stop();
            }
            else {
                giveInputLabel.setVisible(true);
                inputTime.set(10.0);
                giveInputLabel.setDisable(false);
                inputTimeline = new Timeline();
                inputTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(10), new KeyValue(inputTime,0.0)));
                inputTimeline.playFromStart();
            }
        });
    }

    /**
     * Sets up the gui with login information if there was any stored to disk.
     *
     * @param info Loaded login info.
     */
    @Override
    public void loadConnectScreenInfo(ConnectScreenInfo info) {
        Platform.runLater(() -> {
            ipText.setText(info.getIPAddress());
            nick1.setText(info.getLetterOne());
            nick2.setText(info.getLetterTwo());
            nick3.setText(info.getLetterThree());
            connectButton.requestFocus();
        });
    }

    /**
     * Updates the list of all games available to queue for.
     *
     * @param gameList String with a list of all available games.
     */
    @Override
    public void updateGamesList(String gameList) {
        Platform.runLater(() -> {
            String games[] = gameList.split("@");

            //Perform all the hacks
            GameInfo selected = gamesList.getSelectionModel().getSelectedItem();
            gamesList.getItems().clear();
            for (String gameInfo : games) {
                gamesList.getItems().add(new GameInfo(gameInfo));
            }
            gamesList.getSelectionModel().select(selected);

            GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItem();
            if (selectedGame != null)
                gameInfoLabel.setText(selectedGame.getGameInfoString());

            //gamesList.refresh();
        });
    }

    /**
     * Called in order to change the view being displayed to the user.
     *
     * @param newState The view to go to.
     */
    @Override
    public void changeGuiState(GuiState newState) {
        Platform.runLater(() -> {
            ConnectGroup.setVisible(false);
            LobbyGroup.setVisible(false);
            GameGroup.setVisible(false);

            switch (newState) {
                case ConnectScreen:
                    ConnectGroup.setVisible(true);
                    setInputDisabled(false);
                    break;
                case LobbyScreen:
                    LobbyGroup.setVisible(true);
                    waitingForPlayersLabel.setVisible(false);
                    queueForGame.setDisable(false);
                    leaveQueue.setDisable(true);
                    gamesList.setDisable(false);
                    break;
                case GameScreen:
                    GameGroup.setVisible(true);
                    deathLabel.setVisible(false);
                    break;
            }
        });
    }

    /**
     * Updates the list of all the players that are in the game.
     *
     * @param playerList String with list of all players in the game.
     */
    @Override
    public void updatePlayerList(String playerList) {
        Platform.runLater(() -> {
            if (playerColors.size() == 0)
                generatePlayerColors(playerList);
            drawPlayerList(playerListCanvas.getGraphicsContext2D(), playerList);
        });
    }

    /**
     * Updates the player info (all the players stats being displayed)
     *
     * @param playerInfo String with all the new stats.
     */
    @Override
    public void updatePlayerInfo(String playerInfo) {
        Platform.runLater(() -> {
            String info[] = playerInfo.split(",");
            playerInfoLabel.setText("Attacks: " + info[0] + "\n" +
                                    "RandomTeleports: " + info[1] + "\n" +
                                    "SafeTeleports: " + info[2] + "\n" +
                                    "Score: " + info[3]);
        });
    }

    /**
     * Handles player keyboard input.
     *
     * @param code Key that was pressed.
     */
    @Override
    public void keyPressed(KeyCode code){
        if (!GameGroup.isVisible() || playerControls.isDisabled())
            return;

        switch (code){
            case NUMPAD1: buttonMoveDownLeft.fire(); break;
            case NUMPAD2: buttonMoveDown.fire(); break;
            case NUMPAD3: buttonMoveDownRight.fire(); break;
            case NUMPAD4: buttonMoveLeft.fire(); break;
            case NUMPAD5: buttonWait.fire(); break;
            case NUMPAD6: buttonMoveRight.fire(); break;
            case NUMPAD7: buttonMoveUpLeft.fire(); break;
            case NUMPAD8: buttonMoveUp.fire(); break;
            case NUMPAD9: buttonMoveUpRight.fire(); break;
            case NUMPAD0: buttonAttack.fire(); break;
            case ENTER: buttonTeleport.fire(); break;
            case ADD: buttonSafeTeleport.fire(); break;
        }
    }

    /**
     * Displays a popup alert showing the highscores.
     *
     * @param highScoreList String with list of all high scores to be displayed.
     */
    @Override
    public void showHighScoreList(String highScoreList){
        Platform.runLater(() -> this.highScoreList = highScoreList.replace("<", "\n"));
    }

    /**
     * Sets the players Id in the gui.
     * <p>
     * This information is used later when parsing the game state in order to be able
     * to store away the players X and Y coordinates so that they can be used when the
     * player clicks the game board with their mouse.
     *
     * @param playerNum The players Id number.
     */
    @Override
    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
    //endregion

    //region Button-handlers
    /**
     * Handles all player game input.
     *
     * @param actionEvent event.
     */
    public void playerInput(ActionEvent actionEvent) {
        PlayerAction playerAction = null;
        if (actionEvent.getSource().equals(buttonMoveUpLeft))
            playerAction = PlayerAction.MoveUpLeft;
        else if (actionEvent.getSource().equals(buttonMoveUp))
            playerAction = PlayerAction.MoveUp;
        else if (actionEvent.getSource().equals(buttonMoveUpRight))
            playerAction = PlayerAction.MoveUpRight;
        else if (actionEvent.getSource().equals(buttonMoveLeft))
            playerAction = PlayerAction.MoveLeft;
        else if (actionEvent.getSource().equals(buttonWait))
            playerAction = PlayerAction.Wait;
        else if (actionEvent.getSource().equals(buttonMoveRight))
            playerAction = PlayerAction.MoveRight;
        else if (actionEvent.getSource().equals(buttonMoveDownLeft))
            playerAction = PlayerAction.MoveDownLeft;
        else if (actionEvent.getSource().equals(buttonMoveDown))
            playerAction = PlayerAction.MoveDown;
        else if (actionEvent.getSource().equals(buttonMoveDownRight))
            playerAction = PlayerAction.MoveDownRight;
        else if (actionEvent.getSource().equals(buttonAttack))
            playerAction = PlayerAction.Attack;
        else if (actionEvent.getSource().equals(buttonTeleport))
            playerAction = PlayerAction.RandomTeleport;
        else if (actionEvent.getSource().equals(buttonSafeTeleport))
            playerAction = PlayerAction.SafeTeleport;

        ClientEngine.getInstance().performAction(playerAction);
    }

    /**
     * Handles the up-down buttons on the connect screen.
     *
     * @param actionEvent event.
     */
    public void nickButtons(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(nick1Up))
            changeLabelText(+1, nick1);
        else if (actionEvent.getSource().equals(nick2Up))
            changeLabelText(+1, nick2);
        else if (actionEvent.getSource().equals(nick3Up))
            changeLabelText(+1, nick3);
        else if (actionEvent.getSource().equals(nick1Down))
            changeLabelText(-1, nick1);
        else if (actionEvent.getSource().equals(nick2Down))
            changeLabelText(-1, nick2);
        else if (actionEvent.getSource().equals(nick3Down))
            changeLabelText(-1, nick3);
    }

    /**
     * Handles the buttons on the game queueing screen.
     *
     * @param actionEvent event.
     */
    public void gamesQueueButtons(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(queueForGame)) {
            GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItem();
            if (selectedGame == null)
                return;

            ClientEngine.getInstance().queueForGame(selectedGame.getGameId());
            waitingForPlayersLabel.setVisible(true);
            queueForGame.setDisable(true);
            leaveQueue.setDisable(false);
            gamesList.setDisable(true);
        }
        else if (actionEvent.getSource().equals(leaveQueue)) {
            ClientEngine.getInstance().leaveGame();
            waitingForPlayersLabel.setVisible(false);
            queueForGame.setDisable(false);
            leaveQueue.setDisable(true);
            gamesList.setDisable(false);
        }
    }

    /**
     * Fired when the user clicks the connect button on the connect screen.
     *
     * @param actionEvent event.
     */
    public void serverConnect(ActionEvent actionEvent) {
        if (!RegexCheck.ValidIpAndPort(ipText.getText())){
            (new Alert(Alert.AlertType.ERROR, "Invalid IP entered.\nPlease enter an IP following this formatting:\n192.168.1.2:2000")).showAndWait();
            return;
        }

        try {
            ClientEngine.getInstance().connectToServer(ipText.getText());
        } catch (TcpEngineException tcpE) {
            tcpE.printStackTrace();
        } catch (UnknownHostException e) {
            (new Alert(Alert.AlertType.ERROR, "Malformed IP address entered.")).showAndWait();
            return;
        }

        ClientEngine.getInstance().loginToServer(nick1.getText(), nick2.getText(), nick3.getText(), ipText.getText());
    }

    /**
     * Triggered when the user clicks the game board so that the players client can be moved.
     *
     * @param mouseEvent event.
     */
    public void gameBoardClicked(MouseEvent mouseEvent) {
        int xCoordClicked = ((int)(mouseEvent.getX() - xOffset) / Resources.CELL_SIZE);
        int yCoordClicked = ((int)(mouseEvent.getY() - yOffset) / Resources.CELL_SIZE);
        Program.debug("Mouse clicked game board at X: " + xCoordClicked + ", Y: " + yCoordClicked);
        int xDist = xCoordClicked - playerX;
        int yDist = yCoordClicked - playerY;
        Program.debug("xDist = " + xDist + ", yDist = " + yDist);
        if (Math.abs(xDist) > 1 || Math.abs(yDist) > 1)
            return;

        int squareClicked = xDist * 3 + yDist * 7; //math with primes gives unique values for squares.
        switch (squareClicked){
            case -10: buttonMoveUpLeft.fire(); break;
            case -3: buttonMoveLeft.fire(); break;
            case 4: buttonMoveDownLeft.fire(); break;
            case -7: buttonMoveUp.fire(); break;
            case 0: buttonWait.fire(); break;
            case 7: buttonMoveDown.fire(); break;
            case -4: buttonMoveUpRight.fire(); break;
            case 3: buttonMoveRight.fire(); break;
            case 10: buttonMoveDownRight.fire(); break;
        }
    }

    /**
     * Handles when either of the leave game buttons are pressed.
     *
     * @param actionEvent event.
     */
    public void leaveGameHandler(ActionEvent actionEvent) {
        ClientEngine.getInstance().leaveGame();
        //changeGuiState();
    }

    /**
     * Triggered when the user clicks on the games list to select a game to queue for.
     *
     * @param event event.
     */
    public void gamesListClicked(Event event) {
        if (!event.getSource().equals(gamesList))
            return;

        GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItem();
        if (selectedGame == null)
            return;
        gameInfoLabel.setText(selectedGame.getGameInfoString());

        if(selectedGame.getPlayersQueued() >= selectedGame.getPlayersNeeded()){
            queueForGame.setDisable(true);
        }
        else{
            queueForGame.setDisable(false);
        }
    }
    //endregion

    //region Private methods
    private void changeLabelText(int change, Label label){
        char c = label.getText().charAt(0);
        c += change;
        if (c > 'Z')
            c = 'A';
        else if (c < 'A')
            c = 'Z';
        label.setText(String.valueOf(c));
    }

    private void drawGameBoard(GraphicsContext gc, String gameState){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        int x = 0, y = 0;
        gc.setStroke(Color.BLACK);

        for (int i = 0; i < gameState.length(); i++) {
            if (gameState.charAt(i) == '>'){
                y++;
                x = 0;
            }
            else {
                //Keep reading the entire numeric ID of the player
                if (gameState.charAt(i) == '[') {
                    String buf = "";
                    i++;
                    do {
                        buf += gameState.charAt(i++);
                    } while (gameState.charAt(i) != ']');
                    int playerId = Integer.parseInt(buf);

                    if (playerId == playerNum) {
                        playerX = x;
                        playerY = y;
                    }
                    renderPlayer(gc, x, y, playerColors.get(playerId), playerId == playerNum);
                }
                else {
                    switch (gameState.charAt(i)) {
                        case '@': //robot
                            renderRobot(gc, x, y);
                            break;
                        case '#': //rubble
                            renderRubble(gc, x, y);
                            break;
                    }
                }
                gc.strokeRect(x++ * Resources.CELL_SIZE + xOffset,    //top left X
                              y * Resources.CELL_SIZE + yOffset,      //top left Y
                              Resources.CELL_SIZE,                    //width
                              Resources.CELL_SIZE);                   //height
            }
        }
    }

    private void renderRubble(GraphicsContext gc, int x, int y){
        gc.setFill(Color.BLACK);
        gc.setLineWidth(3.0);
        //diagonal 1
        gc.strokeLine(x * Resources.CELL_SIZE + xOffset,              //top left X
                      y * Resources.CELL_SIZE + yOffset,              //top left Y
                      x * Resources.CELL_SIZE + Resources.CELL_SIZE + xOffset,  //bottom right X
                      y * Resources.CELL_SIZE + Resources.CELL_SIZE + yOffset); //bottom right Y
        //diagonal 2
        gc.strokeLine(x * Resources.CELL_SIZE + xOffset,              //bottom left X
                      y * Resources.CELL_SIZE + Resources.CELL_SIZE + yOffset,  //bottom left Y
                      x * Resources.CELL_SIZE + Resources.CELL_SIZE + xOffset,  //top right X
                      y * Resources.CELL_SIZE + yOffset);             //top right Y
        gc.setLineWidth(1.0);
    }

    private void renderRobot(GraphicsContext gc, int x, int y){
        gc.setFill(Color.RED);
        //color fill
        gc.fillPolygon(new double[]{ x * Resources.CELL_SIZE + xOffset,                             //top left X
                                     x * Resources.CELL_SIZE + Resources.CELL_SIZE + xOffset,       //top right X
                                     x * Resources.CELL_SIZE + Resources.CELL_SIZE / 2 + xOffset},  //bottom center X
                       new double[]{ y * Resources.CELL_SIZE + yOffset,                             //top left Y
                                     y * Resources.CELL_SIZE + yOffset,                             //top right Y
                                     y * Resources.CELL_SIZE + Resources.CELL_SIZE  + yOffset},     //bottom center Y
                                     3);                                                            //num vertices
        //outline
        gc.strokePolygon(new double[]{ x * Resources.CELL_SIZE + xOffset,                           //top left X
                                       x * Resources.CELL_SIZE + Resources.CELL_SIZE + xOffset,     //top right X
                                       x * Resources.CELL_SIZE + Resources.CELL_SIZE / 2 + xOffset},//bottom center X
                         new double[]{ y * Resources.CELL_SIZE + yOffset,                           //top left Y
                                       y * Resources.CELL_SIZE + yOffset,                           //top right Y
                                       y * Resources.CELL_SIZE + Resources.CELL_SIZE  + yOffset},   //bottom center Y
                                       3);                                                          //num vertices
    }

    private void renderPlayer(GraphicsContext gc, int x, int y, Color playerColor, boolean isUserControlled){
        gc.setFill(playerColor);
        //color fill
        gc.fillOval(x * Resources.CELL_SIZE + xOffset,    //top left X
                    y * Resources.CELL_SIZE + yOffset,    //top left Y
                    Resources.CELL_SIZE,                  //X-radius
                    Resources.CELL_SIZE);                 //Y-radius
        //outline
        gc.strokeOval(x * Resources.CELL_SIZE + xOffset,  //top left X
                      y * Resources.CELL_SIZE + yOffset,  //top left Y
                      Resources.CELL_SIZE,                //X-radius
                      Resources.CELL_SIZE);               //Y-radius
        if (isUserControlled){
            gc.setFill(playerColor.invert());
            gc.fillOval(x * Resources.CELL_SIZE + Resources.CELL_SIZE / 4 + xOffset,  //top left X
                        y * Resources.CELL_SIZE + Resources.CELL_SIZE / 4 + yOffset,  //top left Y
                        Resources.CELL_SIZE / 2,                                      //X-radius
                        Resources.CELL_SIZE / 2);                                     //Y-radius
        }
    }


    private void drawPlayerList(GraphicsContext gc, String playerList) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        String players[] = playerList.split("@");
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < players.length; i++) {
            //<playerNum>,<playerName>,<playerStatus>@
            String info[] = players[i].split(",");
            int playerId = Integer.parseInt(info[0]);
            Color playerColor = playerColors.get(playerId);
            gc.setFill(playerColor);
            gc.fillOval(5, i * Resources.CELL_SIZE, Resources.CELL_SIZE, Resources.CELL_SIZE);
            gc.strokeOval(5, i * Resources.CELL_SIZE, Resources.CELL_SIZE, Resources.CELL_SIZE);
            if (playerId == this.playerNum){
                gc.setFill(playerColor.invert());
                gc.fillOval(5 + Resources.CELL_SIZE / 4,
                            i * Resources.CELL_SIZE + Resources.CELL_SIZE / 4,
                            Resources.CELL_SIZE / 2,
                            Resources.CELL_SIZE / 2);
        }
        gc.setFill(Color.BLACK);
            gc.fillText(info[1] +  " - " + info[2], 10 + Resources.CELL_SIZE, (i + 1) * Resources.CELL_SIZE );
        }
    }

    private void generatePlayerColors(String playerList) {
        String players[] = playerList.split("@");
        for (String player : players) {
            //<playerNum>,<playerName>,<playerStatus>@
            String info[] = player.split(",");
            Color newColor;
            Random rng = new Random(System.currentTimeMillis());
            do {
                newColor = Color.rgb(Math.abs(rng.nextInt() % 256), Math.abs(rng.nextInt() % 256), Math.abs(rng.nextInt() % 256));
            } while (newColor.equals(Color.BLACK) || playerColors.containsValue(newColor));
            playerColors.put(Integer.parseInt(info[0]), newColor);
        }
    }
    //endregion
}
