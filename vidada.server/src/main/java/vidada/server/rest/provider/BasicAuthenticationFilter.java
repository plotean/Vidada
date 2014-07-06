package vidada.server.rest.provider;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import vidada.model.user.User;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IUserService;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

/**
 * This filter manages authentication, and sets the principal user to the
 * Security-Context.
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthenticationFilter implements ContainerRequestFilter  {

    private final IUserService userService = VidadaRestServer.VIDADA_SERVER.getUserService();
    private final static Logger logger = LogManager.getLogger(BasicAuthenticationFilter.class.getName());


    @Context
    UriInfo uriInfo;
    @Context
    HttpServletRequest request;

    private static final String REALM = "HTTPS API authentication";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        try{
            User user = authenticate(containerRequestContext);
            if(user != null){
                containerRequestContext.setSecurityContext(new Authorizer(user));
            }
        }catch (Throwable e){
            logger.error(e);
            throw e;
        }
    }

    private User authenticate(ContainerRequestContext containerRequestContext) {
        // Extract authentication credentials

        String authentication = containerRequestContext.getHeaderString(ContainerRequest.AUTHORIZATION);
        if (authentication == null) {
            throw new WebApplicationException("Authentication credentials are required", buildUNAUTHORIZED());
        }
        if (!authentication.startsWith("Basic ")) {
            return null;
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        authentication = authentication.substring("Basic ".length());


        String[] values = new String(Base64.getDecoder().decode(authentication)).split(":");
        if (values.length < 2) {
            throw new WebApplicationException("Invalid syntax for username and password", 400);
        }
        String username = values[0];
        String password = values[1];
        if ((username == null) || (password == null)) {
            throw new WebApplicationException("Missing username or password", 400);
        }

        // Validate the extracted credentials
        return authenticate(request.getRemoteAddr(), username, password);
    }

    private User authenticate(String origin, String username, String password) throws WebApplicationException{

        User authenticatedUser = userService.authenticate(origin, username, password);

        if(authenticatedUser == null){
            logger.warn("Could not authenticate " + username + " from " + origin);
            throw new WebApplicationException("Invalid username or password!", buildUNAUTHORIZED());
        }

        logger.info("User " + authenticatedUser.getName() + " from " + origin + " authenticated successful!");

        return authenticatedUser;
    }

    private Response buildUNAUTHORIZED(){
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"" + REALM + "\"" )
                .build();
    }

    class Authorizer implements SecurityContext {

        private final User user;

        public Authorizer(final User user) {
            this.user = user;
        }

        public Principal getUserPrincipal() {
            return this.user;
        }

        public boolean isUserInRole(String role) {
            return true;
            //TODO user.getRoles().contains(role);
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }
}