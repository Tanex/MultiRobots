package nu.tanex.client.gui;

import javafx.application.Platform;
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
import nu.tanex.client.Program;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.client.gui.data.ConnectScreenInfo;
import nu.tanex.client.gui.data.GameInfo;
import nu.tanex.client.resources.GuiState;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public class ClientController implements IClientGuiController {
    private static final int CELL_SIZE = 15;

    private int playerNum = -1;
    private int playerX = -1;
    private int playerY = -1;

    private HashMap<Integer, Color> playerColors = new HashMap<>();

    //region GUI controls
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

    //region Interface IClientGuiController
    @Override
    public void updateGameState(String gameState) {
        Platform.runLater(() -> {
            // TODO: 2016-01-20 try to center canvas somewhat
            drawGameBoard(gameCanvas.getGraphicsContext2D(), gameState);
        });
    }

    @Override
    public void setInputDisabled(boolean disabled) {
        Platform.runLater(() -> playerControls.setDisable(disabled));
    }

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

    @Override
    public void updateGamesList(String gameList) {
        Platform.runLater(() -> {
            String games[] = gameList.split("@");
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
                    break;
            }
        });
    }

    @Override
    public void updatePlayerList(String playerList) {
        Platform.runLater(() -> {
            if (playerColors.size() == 0)
                generatePlayerColors(playerList);
            drawPlayerList(playerListCanvas.getGraphicsContext2D(), playerList);
        });
    }

    private void drawPlayerList(GraphicsContext gc, String playerList) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        String players[] = playerList.split("@");
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < players.length; i++) {
            //<playerNum>,<playerName>,<playerStatus>@
            String info[] = players[i].split(",");
            gc.setFill(playerColors.get(Integer.parseInt(info[0])));
            gc.fillOval(5, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            gc.strokeOval(5, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            gc.setFill(Color.BLACK);
            gc.fillText(info[1] +  " - " + info[2], 10 + CELL_SIZE, (i + 1) * CELL_SIZE );
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
            case PLUS: buttonSafeTeleport.fire(); break;
        }
    }

    @Override
    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
    //endregion

    //region Button-handlers
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
        } else if (actionEvent.getSource().equals(leaveQueue)) {
            ClientEngine.getInstance().leaveGame();
            waitingForPlayersLabel.setVisible(false);
            queueForGame.setDisable(false);
            leaveQueue.setDisable(true);
            gamesList.setDisable(false);
        }
    }

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

    public void gameBoardClicked(MouseEvent mouseEvent) {
        int xCoordClicked = (int)(mouseEvent.getX() / CELL_SIZE);
        int yCoordClicked = (int)(mouseEvent.getY() / CELL_SIZE);
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
    //endregion

    public void leaveGameHandler(ActionEvent actionEvent) {
        ClientEngine.getInstance().leaveGame();
        //changeGuiState();
    }

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
                    gc.setFill(playerColors.get(playerId));
                    gc.fillOval(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    gc.strokeOval(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
                else {
                    switch (gameState.charAt(i)) {
                        case '@': //robot
                            gc.setFill(Color.RED);
                            gc.fillPolygon(new double[]{ x * CELL_SIZE, x * CELL_SIZE + CELL_SIZE, x * CELL_SIZE + CELL_SIZE / 2},
                                            new double[]{ y * CELL_SIZE, y * CELL_SIZE , y * CELL_SIZE + CELL_SIZE }, 3);
                            break;
                        case '#': //rubble
                            gc.setFill(Color.BLACK);
                            gc.setLineWidth(3.0);
                            gc.strokeLine(x * CELL_SIZE, y * CELL_SIZE, x * CELL_SIZE + CELL_SIZE, y * CELL_SIZE + CELL_SIZE);
                            gc.strokeLine(x * CELL_SIZE, y * CELL_SIZE + CELL_SIZE, x * CELL_SIZE + CELL_SIZE, y * CELL_SIZE);
                            gc.setLineWidth(1.0);
                            break;
                    }
                }
                gc.strokeRect(x++ * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    //endregion
}
