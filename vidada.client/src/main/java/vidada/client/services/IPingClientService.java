package vidada.client.services;

/**
 * Provides functionality to check if the connection to the server is working.
 */
public interface IPingClientService {

    /**
     * Pings the server, and if success it will return true.
     * @return
     */
    boolean ping();
}
