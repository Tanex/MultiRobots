package nu.tanex.server.io;

import nu.tanex.server.core.Client;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-11-26
 */
public interface IServerClientThread {
    Client getClient();
    void sendMsg(String msg);
}
