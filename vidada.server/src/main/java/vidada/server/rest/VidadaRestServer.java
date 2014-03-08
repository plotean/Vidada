package vidada.server.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;

import vidada.IVidadaServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;


public class VidadaRestServer{

	public static IVidadaServer VIDADA_SERVER;
	private IVidadaServer server;

	public VidadaRestServer(IVidadaServer server){
		this.server = server;
		VIDADA_SERVER = server;
	}



	public HttpServer start(){

		System.out.println("Configuration of REST Server...");

		String serverLocation = "http://localhost:5555/api";
		DefaultResourceConfig rc = new PackagesResourceConfig("vidada.server.rest.resource");

		final Map<String, Object> config = new HashMap<String, Object>();
		config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
		rc.setPropertiesAndFeatures(config);

		// The following line is to enable GZIP when client accepts it
		//resourceConfig.getContainerResponseFilters().add(new GZIPContentEncodingFilter());

		rc.getProperties().put(
				"com.sun.jersey.spi.container.ContainerRequestFilters", "vidada.server.rest.AuthFilter");

		HttpServer server = null;
		try {
			System.out.println("Starting REST Server @ "  + serverLocation);
			server = GrizzlyServerFactory.createHttpServer(
					serverLocation, rc);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
