package nu.tanex.engine.resources;

/**
 * Exception thrown by ClientTcpEngine and ServerTcpEngine if there are any
 * errors during initialization or while running.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class TcpEngineException extends Exception {
    public TcpEngineException(String msg){
        super(msg);
    }

    public TcpEngineException(Throwable cause){
        super(cause);
    }

    public TcpEngineException(String msg, Throwable cause){
        super(msg, cause);
    }
}
