package nu.tanex.server.core;

import nu.tanex.engine.data.Player;
import nu.tanex.engine.resources.TcpEngineException;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * Handles a connected client, is used to keep track of a players gamestate
 * and to handle the thread responsible for communicating with the player
 * over the network.
 *
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-11-26
 */
public class Client extends Player {
}
