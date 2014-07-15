package vidada.client.local.impl;

import vidada.client.services.IPingClientService;

/**
 *
 */
public class LocalPingClientService implements IPingClientService {
    @Override
    public boolean ping() {
        return true;
    }
}
