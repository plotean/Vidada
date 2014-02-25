package vidada.server.rest;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vidada.IVidadaServer;
import vidada.server.rest.resource.HelloWorldResource;
import vidada.server.rest.resource.MediaResource;
import vidada.server.rest.resource.MediasResource;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.simple.container.SimpleServerFactory;

public class VidadaRestServer{

	public static IVidadaServer VIDADA_SERVER;
	private IVidadaServer server;

	public VidadaRestServer(IVidadaServer server){
		this.server = server;
		VIDADA_SERVER = server;
	}



	public Closeable start(){

		System.out.println("Configuration of REST Server...");

		String serverLocation = "http://0.0.0.0:5555";

		DefaultResourceConfig resourceConfig = new DefaultResourceConfig(
				HelloWorldResource.class,
				MediaResource.class,
				MediasResource.class
				);


		final Map<String, Object> config = new HashMap<String, Object>();
		config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
		resourceConfig.setPropertiesAndFeatures(config);

		// The following line is to enable GZIP when client accepts it
		//resourceConfig.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
		Closeable server = null;
		try {
			System.out.println("Starting REST Server @ "  + serverLocation);
			server = SimpleServerFactory.create(serverLocation, resourceConfig);
			//System.out.println("Press any key to stop the service...");
			//System.in.read();
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
