package nu.tanex.client.resources;

import java.util.regex.Pattern;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public abstract class RegexCheck {
    public static boolean GameState(String msg) {
        return Pattern.compile("^GameState:.*$").matcher(msg).matches();
    }

    public static boolean GiveInput(String msg) {
        return Pattern.compile("^GiveInput$").matcher(msg).matches();
    }

    public static boolean NoMoreInput(String msg) {
        return Pattern.compile("^NoMoreInput$").matcher(msg).matches();
    }

    public static boolean ValidIpAndPort(String msg){
        return Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{1,5}$").matcher(msg).matches();
    }

    public static boolean GamesList(String msg){
        return Pattern.compile("^GamesList:.*$").matcher(msg).matches();
    }

    public static boolean Welcome(String msg){
        return Pattern.compile("^Welcome$").matcher(msg).matches();
    }

    public static boolean GameStart(String msg){
        return Pattern.compile("^GameStart:.*$").matcher(msg).matches();
    }

    public static boolean PlayerList(String msg){
        return Pattern.compile("^PlayerList:.*$").matcher(msg).matches();
    }

    public static boolean PlayerInfo(String msg){
        return Pattern.compile("^PlayerInfo:.*$").matcher(msg).matches();
    }

    public static boolean Kicked(String msg){
        return Pattern.compile("^Kicked$").matcher(msg).matches();
    }
}
