package vidada.client.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.services.IPingClientService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URI;

public class PingServiceRestClient extends AbstractRestService implements IPingClientService{

    private static final Logger logger = LogManager.getLogger(PingServiceRestClient.class.getName());


    public PingServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public boolean ping() {

        try {
            String response = pingResource().request(MediaType.TEXT_PLAIN).get(String.class);
            return response != null && response.equals("success");
        }catch (Exception e) {
            logger.warn("Ping to " + pingResource().getUri() + " failed: " +  e.getMessage());
        }
        return false;
	}

	protected WebTarget pingResource(){
		return apiResource().path("ping");
	}
}
