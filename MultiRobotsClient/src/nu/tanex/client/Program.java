package nu.tanex.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.client.io.ClientTcpEngine;
import nu.tanex.core.exceptions.TcpEngineException;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Class containing the main method.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Program extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/ClientFormMain.fxml"));
        Parent root = loader.load();
        ClientEngine.getInstance().setGuiController(loader.getController());
        primaryStage.setTitle("MultiRobots v0.1");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    /**
     * Main entry point of the client program.
     * */
    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World!");
        ClientEngine.getInstance().setTcpEngine(new ClientTcpEngine());
        launch(args);

        ClientEngine.getInstance().exit();
        System.exit(1);
    }
}
