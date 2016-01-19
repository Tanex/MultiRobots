package nu.tanex.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.tanex.core.exceptions.TcpEngineException;
import nu.tanex.server.core.ServerEngine;
import nu.tanex.server.core.ServerThread;
import nu.tanex.server.io.ServerTcpEngine;

/**
 * Class containing the main method
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Program extends Application {
    private static final boolean DO_DEBUG_PRINTOUT = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/ServerFormMain.fxml"));
        Parent root = loader.load();
        ServerEngine.getInstance().setGuiController(loader.getController());
        primaryStage.setTitle("MultiRobotsServer v0.1");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    /**
     * Main entry point of server program
     * */
    public static void main(String[] args) throws TcpEngineException {
        ServerEngine.getInstance().setTcpEngine(new ServerTcpEngine());
        ServerEngine.getInstance().setServerThread(new ServerThread("serverDefault"));

        launch(args);

        ServerEngine.getInstance().exit();
        System.exit(1);
    }

    /**
     * Used for basic debugging printout, will be optimized away if DO_DEBUG_PRINTOUT is set to false.
     * <p>
     * Gives printout in the format:
     * Debug - <calling class>: <message>
     *
     * @param msg Message to be printed.
     */
    public static void debug(String msg){
        if (!DO_DEBUG_PRINTOUT)
            return;
        String caller = new Exception().getStackTrace()[1].getClassName();
        System.out.println("Debug - " + caller + ": " + msg);
}
}
