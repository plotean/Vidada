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

		final long totalLength = resource.length();

		//Range
		String requestRange = request.getHeader(Header.Range);
		HttpByteRange range = HttpByteRange.parse(requestRange);
		// Ensure that we send to an index > 0
		if(range.to == 0){
			range.to = totalLength;
		}

		// Ensure range is in valid bounds
		range.from = Math.max(0, range.from); 			// Smallest value is zero
		range.from = Math.min(totalLength, range.from);	// Biggest value is resource.length
		range.to = Math.max(0, range.to);				// Smallest value is zero
		range.to = Math.min(totalLength, range.to);		// Biggest value is resource.length


		System.out.println("requested range: " + requestRange);
		System.out.println("interpreted range: " + range);
		
		long requestLenght = range.to - range.from;

		try {
			response.addHeader(Header.AcceptRanges, "bytes");
			response.setHeader(Header.ContentRange, range.toByteRange(totalLength));
			response.setHeader(Header.ContentLength, requestLenght+"");
			response.addHeader(Header.ContentType, resource.getMimeType());

			response.flush(); // Flush the response header.
			
			sendStream(response, resource, range);

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

	/**
	 * Send the given range from the given resource as stream
	 * @param response
	 * @param resource The resource to stream
	 * @param range The range of the resource to stream
	 */
	private void sendStream(final Response response, final ResourceLocation resource, HttpByteRange range) {
		final InputStream inputStream = resource.openInputStream();
		OutputStream outputStream = null;

		try {
			if(inputStream instanceof ISeekableInputStream)
				((ISeekableInputStream) inputStream).seek(range.from);

			// Stream the file content
			outputStream = response.getOutputStream();
			IOUtils.copy(inputStream, outputStream);

		} catch (IOException e) {
			System.err.println("SeekableHttpHandler:send -> " + e.getMessage());
		}finally{
			
			response.finish();
			
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
