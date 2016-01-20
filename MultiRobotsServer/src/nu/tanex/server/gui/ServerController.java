package nu.tanex.server.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import nu.tanex.core.resources.PlayerAttacks;
import nu.tanex.core.resources.RobotAiMode;
import nu.tanex.core.resources.RobotCollisions;
import nu.tanex.server.aggregates.GameManagerList;
import nu.tanex.server.core.ServerEngine;
import nu.tanex.server.gui.data.GameInfo;
import nu.tanex.server.gui.data.PlayerInfo;
import nu.tanex.server.gui.data.SettingsInfo;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class ServerController implements IServerGuiController, Initializable {
    public ListView<GameInfo> gamesList;
    public Label gameNameLabel;
    public Button saveButton;
    public Button discardButton;
    public Button kickButton;
    public Label gameInfoLabel;
    public Label playerInfoLabel;
    public Label gridFillLabel;
    public ListView<PlayerInfo> playerList;
    public Label serverInfoLabel;
    public ComboBox<PlayerAttacks> attackModeComboBox;
    public ComboBox<RobotAiMode> robotAIModeComboBox;
    public ComboBox<RobotCollisions> robotCollisionModeComboBox;
    public Slider attacksAwardedSlider;
    public TextField attacksAwardedTextField;
    public Slider safeTeleportsSlider;
    public TextField safeTeleportsTextField;
    public Slider randomTeleportsSlider;
    public TextField randomTeleportsTextField;
    public Slider additionalRobotsSlider;
    public TextField additionalRobotsTextField;
    public Slider initialRobotsSlider;
    public TextField initialRobotsTextField;
    public Slider initialRubbleSlider;
    public TextField initialRubbleTextField;
    public Slider gridWidthSlider;
    public TextField gridWidthTextField;
    public Slider gridHeightSlider;
    public TextField gridHeightTextField;


    @Override
    public void updateGameList(GameManagerList games, int numConnectedClients) {
        // TODO: 2016-01-20 follow through and update everything else too
        Platform.runLater(() -> {
            //Perform all the hacks
            GameInfo selected = gamesList.getSelectionModel().getSelectedItem();
            gamesList.getItems().clear();
            for (int i = 0; i < games.size(); i++) {
                gamesList.getItems().add(new GameInfo(i, games.get(i)));
            }
            int numPlaying = gamesList.getItems().stream().mapToInt(GameInfo::getNumPlayers).sum();
            int numQueueing = gamesList.getItems().stream().mapToInt(GameInfo::getNumQueueing).sum();
            serverInfoLabel.setText("Clients connected: " + numConnectedClients + " --- Players in games: " + numPlaying + " --- Players queueing: " + numQueueing);
            gamesList.getSelectionModel().select(selected);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attackModeComboBox.getItems().addAll(PlayerAttacks.values());
        robotAIModeComboBox.getItems().addAll(RobotAiMode.values());
        robotCollisionModeComboBox.getItems().addAll(RobotCollisions.values());

        setupSliderAndTextField(attacksAwardedSlider, attacksAwardedTextField);
        setupSliderAndTextField(safeTeleportsSlider, safeTeleportsTextField);
        setupSliderAndTextField(randomTeleportsSlider, randomTeleportsTextField);
        setupSliderAndTextField(additionalRobotsSlider, additionalRobotsTextField);
        setupSliderAndTextField(initialRobotsSlider, initialRobotsTextField);
        setupSliderAndTextField(initialRubbleSlider, initialRubbleTextField);
        setupSliderAndTextField(gridWidthSlider, gridWidthTextField);
        setupSliderAndTextField(gridHeightSlider, gridHeightTextField);
        
        setupGridFillCalc();
    }

    private void setupGridFillCalc(){
        initialRobotsSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRobotsTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRubbleSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRubbleTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        gridWidthSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        gridWidthTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        gridHeightSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        gridHeightTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
    }
    
    private void updateGridFill(){
        int robots = Integer.parseInt(initialRobotsTextField.getText());
        int rubble = Integer.parseInt(initialRubbleTextField.getText());
        int width = Integer.parseInt(gridWidthTextField.getText());
        int height = Integer.parseInt(gridHeightTextField.getText());

        double fillPercent = (100.0 * (robots + rubble + GameInfo.getPlayersToStart())) / (width * height);

        gridFillLabel.setText(String.format("Grid Fill: %.2f %%", fillPercent));
    }
    
    private void setupSliderAndTextField(Slider slider, TextField textField){
        slider.valueProperty().addListener((o, ov, nv) -> textField.setText(Integer.toString(nv.intValue())));
        textField.textProperty().addListener((o, ov, nv) -> slider.valueProperty().set(Integer.parseInt(nv)));
        textField.setText(Integer.toString((int)slider.valueProperty().get()));
    }

    public void listClicked(Event event) {
        if (event.getSource().equals(gamesList)) {
            GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItem();
            if (selectedGame == null)
                return;

            playerList.getItems().clear();
            playerList.getItems().addAll(selectedGame.getPlayerList());
            gameInfoLabel.setText(selectedGame.getInfoString());
            loadGameSettings(selectedGame.getSettingsInfo());
        }
        else if (event.getSource().equals(playerList)){
            PlayerInfo selectedPlayer = playerList.getSelectionModel().getSelectedItem();
            playerInfoLabel.setText(selectedPlayer.getInfoString());
        }
    }

    private void loadGameSettings(SettingsInfo settingsInfo) {
        attackModeComboBox.setValue(settingsInfo.playerAttacks);
        robotAIModeComboBox.setValue(settingsInfo.robotAiMode);
        robotCollisionModeComboBox.setValue(settingsInfo.robotCollisions);
        attacksAwardedSlider.setValue(settingsInfo.numAttacksAwarded);
        safeTeleportsSlider.setValue(settingsInfo.numSafeTeleportsAwarded);
        randomTeleportsSlider.setValue(settingsInfo.numRandomTeleportsAwarded);
        initialRobotsSlider.setValue(settingsInfo.numAdditionalRobotsPerLevel);
        initialRobotsSlider.setValue(settingsInfo.numInitialRobots);
        initialRubbleSlider.setValue(settingsInfo.numInitialRubble);
        gridWidthSlider.setValue(settingsInfo.gridWidth);
        gridHeightSlider.setValue(settingsInfo.gridHeight);
    }

    public void buttonClicked(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(kickButton)){
            PlayerInfo selectedPlayer = playerList.getSelectionModel().getSelectedItem();
            if (selectedPlayer == null)
                return;
            ServerEngine.getInstance().kickPlayer(selectedPlayer, gamesList.getSelectionModel().getSelectedItem());
        }
        else if (actionEvent.getSource().equals(saveButton)){
            SettingsInfo newSettings = new SettingsInfo();
            
            newSettings.numInitialRobots = (int)initialRobotsSlider.getValue();
            newSettings.numAdditionalRobotsPerLevel = (int)initialRobotsSlider.getValue();
            newSettings.numInitialRubble = (int)initialRubbleSlider.getValue();
            newSettings.robotCollisions = robotCollisionModeComboBox.getValue();
            newSettings.numSafeTeleportsAwarded = (int)safeTeleportsSlider.getValue();
            newSettings.numRandomTeleportsAwarded = (int)randomTeleportsSlider.getValue();
            newSettings.numAttacksAwarded = (int)attacksAwardedSlider.getValue();
            newSettings.playerAttacks = attackModeComboBox.getValue();
            newSettings.robotAiMode = robotAIModeComboBox.getValue();
            newSettings.gridWidth = (int)gridWidthSlider.getValue();
            newSettings.gridHeight = (int)gridHeightSlider.getValue();

            ServerEngine.getInstance().updateGameSetting(gamesList.getSelectionModel().getSelectedItem(), newSettings);
        }
        else if (actionEvent.getSource().equals(discardButton)){
            loadGameSettings(gamesList.getSelectionModel().getSelectedItem().getSettingsInfo());
        }
    }
}
