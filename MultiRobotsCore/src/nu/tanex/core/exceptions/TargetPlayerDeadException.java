package nu.tanex.core.exceptions;

/**
 * Exception thrown by ClientTcpEngine and ServerTcpEngine if there are any
 * errors during initialization or while running.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class TargetPlayerDeadException extends Exception {
    public TargetPlayerDeadException(String msg){
        super(msg);
    }

    public TargetPlayerDeadException(Throwable cause){
        super(cause);
    }

    public TargetPlayerDeadException(String msg, Throwable cause){
        super(msg, cause);
    }
}
