package nu.tanex.core.exceptions;

/**
 * Exception thrown by ClientTcpEngine and ServerTcpEngine if there are any
 * errors during initialization or while running.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class GameException extends Exception {
    public GameException(String msg){
        super(msg);
    }

    public GameException(Throwable cause){
        super(cause);
    }

    public GameException(String msg, Throwable cause){
        super(msg, cause);
    }
}
