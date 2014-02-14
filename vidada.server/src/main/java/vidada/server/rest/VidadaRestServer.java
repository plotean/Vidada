package vidada.server.rest;

import java.io.Closeable;
import java.io.IOException;

import vidada.server.rest.resource.HelloWorldResource;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.simple.container.SimpleServerFactory;

public class VidadaRestServer{

	private void start(){

		DefaultResourceConfig resourceConfig = new DefaultResourceConfig(HelloWorldResource.class);
		// The following line is to enable GZIP when client accepts it
		//resourceConfig.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
		Closeable server = null;
		try {
			server = SimpleServerFactory.create("http://0.0.0.0:5555", resourceConfig);
			System.out.println("Press any key to stop the service...");
			System.in.read();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		System.out.println("Server: Starting...");
		VidadaRestServer server = new VidadaRestServer();
		server.start();
	}
}
