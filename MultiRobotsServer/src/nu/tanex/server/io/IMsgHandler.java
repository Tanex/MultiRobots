package nu.tanex.server.io;

import nu.tanex.server.core.Client;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-19
 */
public interface IMsgHandler {
    void handleMsg(Client client, String msg);
}
