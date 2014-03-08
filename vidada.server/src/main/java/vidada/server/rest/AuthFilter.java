package vidada.server.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class AuthFilter implements ContainerRequestFilter {

	// Exception thrown if user is unauthorized.
	private final static WebApplicationException unauthorized =
			new WebApplicationException(
					Response.status(Status.UNAUTHORIZED)
					.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
					.entity("Page requires login.").build());

	@Override
	public ContainerRequest filter(ContainerRequest containerRequest) 
			throws WebApplicationException {

		if(!isPuplic(containerRequest)){
			// Get the authentication passed in HTTP headers parameters
			String auth = containerRequest.getHeaderValue("authorization");
			if (auth == null)
				throw unauthorized;

			auth = auth.replaceFirst("[Bb]asic ", "");
			String userColonPass = Base64.base64Decode(auth);

			// TODO replace with auth handler
			if (!userColonPass.equals("admin:admin"))
				throw unauthorized;
		}
		return containerRequest;
	}

	/**
	 * Checks if the requests needs authentication
	 * @param containerRequest
	 * @return
	 */
	private boolean isPuplic(ContainerRequest containerRequest){
		String method = containerRequest.getMethod();
		String path = containerRequest.getPath(true).trim();

		if(method.equals("GET")){
			// Allow empty gets of root pages
			if(path.isEmpty()){
				return true;
			}
		}
		return false;
	}
}
