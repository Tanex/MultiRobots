package nu.tanex.client.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.control.*;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.client.data.GameInfo;
import nu.tanex.client.resources.GuiState;
import nu.tanex.client.resources.RegexCheck;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.core.resources.PlayerAction;

import java.net.UnknownHostException;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public class ClientController implements IClientGuiController {
    //region GUI controls
    public Label textLabel;
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
    public Button buttonDebug;
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
    public Label queuedForLabel;
    public Button queueForGame;
    public Button leaveQueue;
    public Label playerListLabel;
    public Label playerInfoLabel;
    //endregion

    //region Interface IClientGuiController
    @Override
    public void updateGameState(String gameState) {
        Platform.runLater(() -> textLabel.setText(gameState.replace('>', '\n')));
    }

    @Override
    public void setInputDisabled(boolean disabled) {
        Platform.runLater(() -> playerControls.setDisable(disabled));
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
                    queuedForLabel.setText("");
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
            String text = "";
            String players[] = playerList.split("@");
            for(String player : players){
                //#<playerNum>,<playerName>,<playerStatus>@
                String info[] = player.split(",");
                text += info[0] + " "+ info[1] +  " - " + info[2] + "\n";
            }
            playerListLabel.setText(text);
        });
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

            ClientEngine.getInstance().queueForGame(selectedGame.getGameId());
            queuedForLabel.setText(selectedGame.toString());
            queueForGame.setDisable(false);
            leaveQueue.setDisable(true);
            gamesList.setDisable(true);
        } else if (actionEvent.getSource().equals(leaveQueue)) {
            ClientEngine.getInstance().leaveGame();
            queuedForLabel.setText("");
            queueForGame.setDisable(true);
            leaveQueue.setDisable(false);
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

        ClientEngine.getInstance().loginToServer(nick1.getText() + nick2.getText() + nick3.getText());
    }
    //endregion

    public void debugButton(ActionEvent actionEvent) {
        ClientEngine.getInstance().queueForGame(0);
    }

    public void gamesListClicked(Event event) {
        if (!event.getSource().equals(gamesList))
            return;
        GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItems().get(0);
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
    //endregion
}
