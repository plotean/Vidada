package vidada.server.rest.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.Header;

import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.io.streaming.ISeekableInputStream;

public abstract class SeekableHttpHandler extends StaticHttpHandler {


	@Override
	public void service(Request request, Response response) throws Exception {
		String requestUri = getRelativeURI(request);

		System.out.println("SeekableHttpHandler: -> requestUri: '"+requestUri+"'");

		ResourceLocation resource = getStreamResource(request, response, requestUri);
		if(resource != null){
			sendStream(request, response, resource);
		}else{
			response.sendError(404);
		}
	}


	private void sendStream(Request request, Response response, ResourceLocation resource) {

		printRequestHeader(request);
		// check header for range request:

		//Range
		String requestRange = request.getHeader(Header.Range);
		HeaderRange range = HeaderRange.parse(requestRange);

		if(range.to < 1){
			range.to = resource.length();
		}

		long streamLenght = range.to - range.from;


		try {
			response.addHeader(Header.AcceptRanges, "bytes");	
			response.setHeader(Header.ContentLength, streamLenght+"");
			response.addHeader(Header.ContentType, resource.getMimeType());

			send(response, resource, range);

			response.sendAcknowledgement();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void printRequestHeader(Request request){
		System.out.println(">> Request Header <<");
		for (String header : request.getHeaderNames()){
			System.out.print(">> " + header + ": ");
			for (String headerValue : request.getHeaders(header)) {
				System.out.print(headerValue + " ; ");
			};
			System.out.println();
		}
		System.out.println(">> Request Header <<");
	}

	/**
	 * Is the given Request valid for this stream handler?
	 * @param request
	 * @param response
	 * @param relativeUri
	 * @return
	 */
	protected abstract ResourceLocation getStreamResource(Request request, Response response, String relativeUri);



	@Override
	protected String getRelativeURI(final Request request) {
		String uri = request.getRequestURI();
		if (uri.contains("..")) {
			return null;
		}

		final String resourcesContextPath = request.getContextPath();
		if (resourcesContextPath != null && !resourcesContextPath.isEmpty()) {
			if (!uri.startsWith(resourcesContextPath)) {
				return null;
			}
			uri = uri.substring(resourcesContextPath.length());
		}

		return uri;
	}


	private void send(final Response response, final ResourceLocation file, HeaderRange range) {
		final InputStream inputStream = file.openInputStream();
		OutputStream outputStream = null;

		try {
			if(inputStream instanceof ISeekableInputStream)
				((ISeekableInputStream) inputStream).seek(range.from);

			response.flush(); // Flush the response header.

			// Stream the file content
			outputStream = response.getOutputStream();
			IOUtils.copy(inputStream, outputStream);

		} catch (IOException e) {
			System.err.println("SeekableHttpHandler:send -> " + e.getMessage());
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			response.finish();
		}
	}


}
