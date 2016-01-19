package nu.tanex.server.gui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import nu.tanex.server.aggregates.GameManagerList;
import nu.tanex.server.core.GameManager;
import nu.tanex.server.gui.data.GameInfo;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class ServerController implements IServerGuiController {
    public ListView<GameInfo> gamesList;
    public Label gameNameLabel;
    public Button saveButton;
    public Button discardButton;


    @Override
    public void updateGameList(GameManagerList games) {
        Platform.runLater(() -> {
            //Perform all the hacks
            GameInfo selected = gamesList.getSelectionModel().getSelectedItem();

            for (GameManager gm : games) {
                gamesList.getItems().add(new GameInfo(gm.getGame()));
            }

            gamesList.getSelectionModel().select(selected);
        });
    }
}
