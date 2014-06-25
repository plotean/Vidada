package vidada.server.rest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Implementation of a Basic Http Authentication filter
 * TODO refactor to actually use a proper user database
 */
public class BasicHttpAuthFilter implements javax.servlet.Filter {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(BasicHttpAuthFilter.class.getName());

    private final String realm = "vidada-api";
    private final String user;
    private final String password;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new BasicHttpAuthFilter
     * @param user
     * @param password
     */
    public BasicHttpAuthFilter(String user, String password){
        this.user = user;
        this.password = password;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        logger.debug("Filtering http authentication...");

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String auth = httpRequest.getHeader( "Authorization" );
        if ( auth != null ) {

            auth = auth.replaceFirst("[Bb]asic ", "");
            String userColonPass = new String(DatatypeConverter.parseBase64Binary(auth));
            String[] parts = userColonPass.split(":");

            if(parts.length > 1) {
                if (authenticate(servletRequest, parts[0], parts[1])) {
                    chain.doFilter(httpRequest, httpResponse);
                    return; // Authorized
                }
            }else{
                logger.error("malformed credentials: " + userColonPass);
            }
        }

        httpResponse.setHeader( "WWW-Authenticate", "Basic realm=\"" + realm + "\"" );
        httpResponse.sendError( HttpServletResponse.SC_UNAUTHORIZED );
    }

    private boolean authenticate(ServletRequest servletRequest, String user, String password){
        return(this.user.equals(user) && this.password.equals(password));
    }


    @Override
    public void destroy() {

    }
}
