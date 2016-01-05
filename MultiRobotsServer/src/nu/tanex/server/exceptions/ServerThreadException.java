package nu.tanex.server.exceptions;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-23
 */
public class ServerThreadException  extends Exception {
    public ServerThreadException(String msg){
        super(msg);
    }

    public ServerThreadException(Throwable cause){
        super(cause);
    }

    public ServerThreadException(String msg, Throwable cause){
        super(msg, cause);
    }
}