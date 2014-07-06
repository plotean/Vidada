package vidada.server.services;

import vidada.model.user.User;

/**
 * Manages all users of the system
 */
public interface IUserService {

    /**
     * Try to authenticate the given user name/password.
     * If successful, a user instance is returned, otherwise null.
     * @param origin
     * @param username
     * @param password
     * @return
     */
    User authenticate(String origin, String username, String password);
}
