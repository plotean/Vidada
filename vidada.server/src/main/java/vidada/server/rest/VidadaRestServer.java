package vidada.server.rest;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import vidada.IVidadaServer;
import vidada.server.rest.streaming.MediaStreamHttpHandler;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;


public class VidadaRestServer {

    private static final String SERVLET_JERSEY = "jersey-servlet";


	public static IVidadaServer VIDADA_SERVER;
	private final IVidadaServer vidadaServer;



	public VidadaRestServer(IVidadaServer server){
		this.vidadaServer = server;
		VIDADA_SERVER = server;
	}


    public HttpServer start() {

        // Create the Web server

        HttpServer server = HttpServer.createSimpleServer("http://0.0.0.0/", 5555);

        // Register a REST handler
        WebappContext webappContext = createRESTContext();
        webappContext.deploy(server);

        // Register a custom Stream handler
        server.getServerConfiguration().addHttpHandler(
                new MediaStreamHttpHandler(vidadaServer.getMediaService()),
                "/stream"); // map the stream handler to /stream

        // Register a static http handler for this jar
        server.getServerConfiguration().addHttpHandler(
                new CLStaticHttpHandler(
                       this.getClass().getClassLoader(), // ClassLoader of the JAR
                       "/www/"),    // absolute path in the jar (see src/main/resources/www)
                "/");               // map the default http to the root


        /* Example for specific directory outside of jar

                // Register a static http handler for a directory
                server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("/var/www/webpage/"), "/xyz");
         */

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return server;
    }

    private WebappContext createRESTContext(){
        WebappContext webappContext = new WebappContext("Grizzly Web Context", "");

        ServletRegistration servletRegistration = webappContext.addServlet(SERVLET_JERSEY, org.glassfish.jersey.servlet.ServletContainer.class);
        servletRegistration.addMapping("/api/*"); // map the REST API to the /api
        servletRegistration.setInitParameter("jersey.config.server.provider.packages", "vidada.server.rest.resource"); // scan this package for resource classes

        FilterRegistration filter = webappContext.addFilter("Auth filter", new BasicHttpAuthFilter("admin", "1337")); // TODO replace with real credential system
        filter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, SERVLET_JERSEY);

        return webappContext;
    }

}
