package nu.tanex.server.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
    //region GUI controls
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
    public Slider playersToStartSlider;
    public TextField playersToStartTextField;
    public Tab settingsTab;
    //endregion

    //region Interface IServerGuiController
    /**
     * Updates the list of games that are displayed in the GUI.
     *
     * @param games List of games running on the server.
     * @param numConnectedClients The number of connected clients on the server
     */
    @Override
    public void updateGameList(GameManagerList games, int numConnectedClients) {
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

            if (selected != null) {
                updateGameInfo();
                updatePlayerList();
            }
        });
    }
    //endregion

    //region Interface Initializable
    /**
     * Initializes a bunch of change listeners and data sources in the gui.
     *
     * @param location location
     * @param resources resources
     */
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
        setupSliderAndTextField(playersToStartSlider, playersToStartTextField);

        setupGridFillCalc();
    }
    //endregion

    //region Public methods
    /**
     * Handles when the users clicks either the game or the player list
     * @param event event
     */
    public void listClicked(Event event) {
        if (event.getSource().equals(gamesList))
            updateGameInfo();
        else if (event.getSource().equals(playerList))
            updatePlayerList();
    }

    /**
     * Handles when the player clicks any button in the gui
     *
     * @param actionEvent event
     */
    public void buttonClicked(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(kickButton)) {
            PlayerInfo selectedPlayer = playerList.getSelectionModel().getSelectedItem();
            if (selectedPlayer == null)
                return;

            Alert kickAlert = new Alert(Alert.AlertType.CONFIRMATION);
            kickAlert.setTitle("Kicking player");
            kickAlert.setContentText("You are about to kick " + selectedPlayer.getName() + "from the game.");
            kickAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            kickAlert.showAndWait();

            if (kickAlert.getResult() == ButtonType.OK)
                ServerEngine.getInstance().kickPlayer(selectedPlayer, gamesList.getSelectionModel().getSelectedItem());
        } else if (actionEvent.getSource().equals(saveButton)) {
            SettingsInfo newSettings = new SettingsInfo();

            newSettings.numInitialRobots = (int) initialRobotsSlider.getValue();
            newSettings.numAdditionalRobotsPerLevel = (int) initialRobotsSlider.getValue();
            newSettings.numInitialRubble = (int) initialRubbleSlider.getValue();
            newSettings.robotCollisions = robotCollisionModeComboBox.getValue();
            newSettings.numSafeTeleportsAwarded = (int) safeTeleportsSlider.getValue();
            newSettings.numRandomTeleportsAwarded = (int) randomTeleportsSlider.getValue();
            newSettings.numAttacksAwarded = (int) attacksAwardedSlider.getValue();
            newSettings.playerAttacks = attackModeComboBox.getValue();
            newSettings.robotAiMode = robotAIModeComboBox.getValue();
            newSettings.gridWidth = (int) gridWidthSlider.getValue();
            newSettings.gridHeight = (int) gridHeightSlider.getValue();
            newSettings.numPlayersToStartGame = (int) playersToStartSlider.getValue();

            ServerEngine.getInstance().updateGameSetting(gamesList.getSelectionModel().getSelectedItem(), newSettings);
        } else if (actionEvent.getSource().equals(discardButton)) {
            loadGameSettings(gamesList.getSelectionModel().getSelectedItem().getSettingsInfo());
        }
    }
    //endregion

    //region Private methods
    private void setupGridFillCalc() {
        initialRobotsSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRobotsTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRubbleSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        initialRubbleTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        gridWidthSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        gridWidthTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        gridHeightSlider.valueProperty().addListener((o, ov, nv) -> updateGridFill());
        gridHeightTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
        playersToStartTextField.textProperty().addListener((o, ov, nv) -> updateGridFill());
    }

    private void updateGridFill() {
        int robots = Integer.parseInt(initialRobotsTextField.getText());
        int rubble = Integer.parseInt(initialRubbleTextField.getText());
        int width = Integer.parseInt(gridWidthTextField.getText());
        int height = Integer.parseInt(gridHeightTextField.getText());
        int players = Integer.parseInt(playersToStartTextField.getText());

        double fillPercent = (100.0 * (robots + rubble + players)) / (width * height);

        gridFillLabel.setText(String.format("Grid Fill: %.2f %%", fillPercent));
    }

    private void setupSliderAndTextField(Slider slider, TextField textField) {
        slider.valueProperty().addListener((o, ov, nv) -> textField.setText(Integer.toString(nv.intValue())));
        textField.textProperty().addListener((o, ov, nv) -> slider.valueProperty().set(Integer.parseInt(nv)));
        textField.setText(Integer.toString((int) slider.valueProperty().get()));
    }

    private void updateGameInfo() {
        GameInfo selectedGame = gamesList.getSelectionModel().getSelectedItem();
        if (selectedGame == null)
            return;

        playerList.getItems().clear();
        playerList.getItems().addAll(selectedGame.getPlayerList());
        gameInfoLabel.setText(selectedGame.getInfoString());
        gameNameLabel.setText("Settings for Game #" + (selectedGame.getGameNum() + 1));
        if (selectedGame.getGameRunning())
            settingsTab.setDisable(true);
        else
            settingsTab.setDisable(false);
        loadGameSettings(selectedGame.getSettingsInfo());
    }

    private void updatePlayerList() {
        PlayerInfo selectedPlayer = playerList.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null)
            playerInfoLabel.setText("");
        else
            playerInfoLabel.setText(selectedPlayer.getInfoString());
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
        playersToStartSlider.setValue(settingsInfo.numPlayersToStartGame);
    }
    //endregion
}
