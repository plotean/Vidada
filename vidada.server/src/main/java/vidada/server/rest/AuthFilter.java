package vidada.server.rest;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ContainerRequest;



public class AuthFilter implements ContainerRequestFilter {

	// Exception thrown if user is unauthorized.
	private final static WebApplicationException unauthorized =
			new WebApplicationException(
					Response.status(Status.UNAUTHORIZED)
					.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
					.entity("Page requires login.").build());

	public ContainerRequest filter(ContainerRequest containerRequest) 
			throws WebApplicationException {

		if(!isPuplic(containerRequest)){
			// Get the authentication passed in HTTP headers parameters
			String auth = containerRequest.getHeaderString("authorization");
			if (auth == null)
				throw unauthorized;

			auth = auth.replaceFirst("[Bb]asic ", "");
			String userColonPass = new String(Base64.getDecoder().decode(auth));

			// TODO replace with auth handler
			if (!userColonPass.equals("admin:1337"))
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

	@Override
	public void filter(ContainerRequestContext arg0) throws IOException {
		// TODO Auto-generated method stub

	}
}
