package nu.tanex.client.io;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class ClientComHandler {
    private ClientTcpEngine tcpEngine;

    public void handleMsg(String msg){
        System.out.println(msg);
    }
}
