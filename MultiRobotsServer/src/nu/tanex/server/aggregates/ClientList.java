package nu.tanex.server.aggregates;

import nu.tanex.server.core.Client;

import java.util.Vector;
import java.util.function.Predicate;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-19
 */
public class ClientList extends Vector<Client> {
    /**
     * Finds the first Client in the list that matches the predicate.
     *
     * @param predicate expression to match Client against.
     * @return First Client that matches.
     */
    public Client find(Predicate<? super Client> predicate){
        for (Client client : this) {
            if (predicate.test(client))
                return client;
        }
        return null;
    }
}
