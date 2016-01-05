package nu.tanex.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.core.resources.PlayerAction;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public class ClientController implements IClientGuiController {
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


    @Override
    public void updateGameState(String gameState) {
        Platform.runLater(() -> textLabel.setText(gameState.replace('>', '\n')));
    }

    @Override
    public void setInputDisabled(boolean disabled) {
        Platform.runLater(() -> playerControls.setDisable(disabled));
    }

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

    public void debugButton(ActionEvent actionEvent) {
        ClientEngine.getInstance().queueForGame(0);
    }
}
