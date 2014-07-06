package vidada.server.services;

import vidada.model.user.User;
import vidada.server.VidadaServer;

/**
 * Implementation of {@link IUserService}
 * TODO Implement User-service
 */
public class UserService extends VidadaServerService implements IUserService{

    /**
     * Creates a new UserService for the given server
     *
     * @param server
     */
    public UserService(VidadaServer server) {
        super(server);
    }

    @Override
    public User authenticate(String origin, String username, String password) {
        // TODO Implement User-service
        return new User(username);
    }
}
