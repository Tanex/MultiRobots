package nu.tanex.core.resources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-23
 */
public class ServerSettings {
    //region Member variables
    private int numGamesToRun = Resources.NUM_GAMES_TO_RUN;
    private int serverPort = Resources.SERVER_PORT;
    //endregion

    //region Get-/setters
    /**
     * Get the port that the server should listen on.
     *
     * @return Port number.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Set what port the server should listen on.
     *
     * @param serverPort Port number.
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Get how many games the server should run simultaneously.
     *
     * @return amount of games to run.
     */
    public int getNumGamesToRun() {
        return numGamesToRun;
    }

    /**
     * Set how many games the server should run simultaneously.
     *
     * @param numGamesToRun amount of games to run.
     */
    public void setNumGamesToRun(int numGamesToRun) {
        this.numGamesToRun = numGamesToRun;
    }

    //endregion

    //region Public methods
    /**
     * Save the all settings in this object to a .ini-file.
     *
     * @param filename Filename to save to without file extension.
     */
    public void saveSettingsToFile(String filename) {
        PrintWriter fout = null;
        try {
            fout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./" + filename + ".ini"))), true);
            fout.println("#Feel free to edit this file manually");
            fout.println("#Just keep the format of <settings name>=<settings value>;");
            fout.println("#All whitespace characters are ignored");
            fout.println("#use # for comments\n");
            fout.print(this.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fout != null)
                fout.close();
        }
    }

    /**
     * Load settings from a .ini-file into this object.
     *
     * @param filename Name of file to load settings from without file extension.
     */
    public void loadSettingsFromFile(String filename) {
        try {
            File fin = new File(filename + ".ini");
            if (!fin.isFile())
                return;
            //Read entire settings file into a string
            String file = new String(Files.readAllBytes(Paths.get(fin.getPath())));
            //Remove all linefeed and newline characters
            file = file.replaceAll("[\\r\\n]{1,2}", "");

            String[] settings = file.split(";|GameSettings");
            for (String str : settings) {
                //Skip if line is a comment
                if (!str.startsWith("#")) {
                    String[] subStrings = str.split("=");
                    String field = subStrings[0].trim();
                    String value = subStrings[1].split(";")[0].trim();
                    readSettingFromString(field, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        return "#GameSettings\n" +
                "numGamesToRun=" + numGamesToRun + ";\n" +
                "serverPort=" + serverPort + ";";
    }
    //endregion

    //region Private methods
    private void readSettingFromString(String field, String value) {
        switch (field) {
            case "numGamesToRun":
                numGamesToRun = Integer.parseInt(value);
                break;
            case "serverPort":
                serverPort = Integer.parseInt(value);
                break;
        }
    }
    //endregion
}
