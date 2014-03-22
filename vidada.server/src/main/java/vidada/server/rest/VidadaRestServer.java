package vidada.server.rest;

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

        WebappContext webappContext = new WebappContext("Grizzly Web Context", "");

        ServletRegistration servletRegistration = webappContext.addServlet(SERVLET_JERSEY, org.glassfish.jersey.servlet.ServletContainer.class);
        servletRegistration.addMapping("/api/*");
        servletRegistration.setInitParameter("jersey.config.server.provider.packages", "vidada.server.rest.resource");

        // Add filters

        // Basic Http Authentication filter
        FilterRegistration filter = webappContext.addFilter("Auth filter", new BasicHttpAuthFilter("admin", "1337"));
        filter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, SERVLET_JERSEY);


        // Create the Web server

        HttpServer server = HttpServer.createSimpleServer("http://0.0.0.0/", 5555);

        webappContext.deploy(server);
        //server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("/var/www/webpage/"), "/xyz");
        server.getServerConfiguration().addHttpHandler(new MediaStreamHttpHandler(vidadaServer.getMediaService()), "/stream");

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return server;
    }

}
