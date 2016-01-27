package nu.tanex.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nu.tanex.client.core.ClientEngine;
import nu.tanex.client.io.ClientTcpEngine;
import nu.tanex.core.resources.Resources;

import java.io.IOException;

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
        Scene newScene = new Scene(root, 1280, 720);
        newScene.addEventHandler(KeyEvent.KEY_PRESSED, ClientEngine.getInstance()::keyboardInput);
        primaryStage.setScene(newScene);
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

    /**
     * Used for basic debugging printout, will be optimized away if DO_DEBUG_PRINTOUT is set to false.
     * <p>
     * Gives printout in the format:
     * Debug - <calling class>: <message>
     *
     * @see Resources
     * @param msg Message to be printed.
     */
    public static void debug(String msg){
        if (!Resources.DO_DEBUG_PRINTOUT)
            return;
        StackTraceElement ste = new Exception().getStackTrace()[1];
        String callingClass = ste.getClassName();
        String caller = ste.getMethodName();
        System.out.println("Debug - " + callingClass + "." + caller + ": " + msg);
    }
}
