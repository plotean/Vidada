package vidada.server.rest;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import vidada.IVidadaServer;
import vidada.server.rest.streaming.MediaStreamHttpHandler;



public class VidadaRestServer extends ResourceConfig{

	public static IVidadaServer VIDADA_SERVER;
	private final IVidadaServer vidadaServer;

	public VidadaRestServer(IVidadaServer server){
		packages("vidada.server.rest.resource");
		this.vidadaServer = server;
		VIDADA_SERVER = server;
	}



	public HttpServer start(){

		System.out.println("Configuration of REST Server...");


		//rc.property("com.sun.jersey.api.json.POJOMappingFeature", true);

		// The following line is to enable GZIP when client accepts it
		//resourceConfig.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
		//rc.getProperties().put("com.sun.jersey.spi.container.ContainerRequestFilters", "vidada.server.rest.AuthFilter");

		HttpServer server = null;
		try {
			URI baseUri = UriBuilder.fromUri("http://0.0.0.0/api").port(5555).build();

			System.out.println("Starting REST Server @ "  + baseUri.toString());

			server = GrizzlyHttpServerFactory.createHttpServer(baseUri, this);

			//server = GrizzlyServerFactory.createHttpServer(serverLocation, rc);
			server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("/Users/IsNull/Movies/"), "/xyz");
			server.getServerConfiguration().addHttpHandler(new MediaStreamHttpHandler(vidadaServer.getMediaService()), "/stream");
			server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(new AuthProbe());


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {

		}

		return server;
	}

	public static void main(String[] args) {
		System.out.println("Server: Starting...");
		//VidadaRestServer server = new VidadaRestServer();
		//server.start();
	}
}
