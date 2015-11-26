package nu.tanex.server.io;

import java.util.Vector;

/**
 * Interface for a generic communication engine for the server.
 *
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public interface IServerComEngine {
    Vector<IServerClientThread> getConnectedClients();
}
