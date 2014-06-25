package vidada.server.rest.streaming;

import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.io.streaming.ISeekableInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.Header;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public abstract class SeekableHttpHandler extends StaticHttpHandler {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/


    private static final Logger logger = LogManager.getLogger(SeekableHttpHandler.class.getName());


    /***************************************************************************
     *                                                                         *
     * Public methods                                                          *
     *                                                                         *
     **************************************************************************/


    @Override
	public void service(Request request, Response response) throws Exception {
		String requestUri = getRelativeURI(request);
		ResourceLocation resource = getStreamResource(request, response, requestUri);
		if(resource != null){
			sendStream(request, response, resource);
		}else{
			response.sendError(404);
		}
	}
	
	
	/**
	 * Is the given Request valid for this stream handler?
	 * @param request
	 * @param response
	 * @param relativeUri
	 * @return
	 */
	protected abstract ResourceLocation getStreamResource(Request request, Response response, String relativeUri);

	/**
	 * Should the sent stream be compressed? 
	 * (Will only compress if the client supports gzip compression)
	 * @return
	 */
	protected abstract boolean isStreamCompressionEnabled();

	/**
	 * Get the relative URI of this request
	 */
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
	


	private void sendStream(Request request, Response response, ResourceLocation resource) {

		printRequestHeader(request);
		// check header for range request:

		final long totalLength = resource.length();
		
		// Parse request header
		boolean compress = isStreamCompressionEnabled() && isGzipSupported(request);
		
		HttpByteRange range = getRequestRange(request, totalLength);
		long requestLenght = range.to - range.from;

		try {
			response.addHeader(Header.AcceptRanges, "bytes");
			response.setHeader(Header.ContentRange, range.toByteRange(totalLength));
			response.setHeader(Header.ContentLength, requestLenght+"");
			response.addHeader(Header.ContentType, resource.getMimeType());

			sendStream(response, resource, range, compress);

			response.sendAcknowledgement();
		} catch (IOException e) {
            logger.error(e);
		}
	}
	
	private boolean isGzipSupported(Request request){
		boolean supportsGzip = false;
		Iterable<String> acceptedEncoding = request.getHeaders(Header.AcceptEncoding);
		for (String encoding : acceptedEncoding) {
			if(encoding.contains("gzip")){
				supportsGzip = true;
				break;
			}
		}
		return supportsGzip;
	}
	
	private HttpByteRange getRequestRange(Request request, long totalLength){
		HttpByteRange range = null;
		String requestRange = request.getHeader(Header.Range);
		if(requestRange != null){ 
			range = HttpByteRange.parse(requestRange);
			// Ensure that we send to an index > 0
			if(range.to == 0){
				range.to = totalLength;
			}
	
			// Ensure range is in valid bounds
			range.from = Math.max(0, range.from); 			// Smallest value is zero
			range.from = Math.min(totalLength, range.from);	// Biggest value is resource.length
			range.to = Math.max(0, range.to);				// Smallest value is zero
			range.to = Math.min(totalLength, range.to);		// Biggest value is resource.length
		}else{
			range = new HttpByteRange(0, totalLength);
		}

        logger.debug("requested range: " + requestRange);
        logger.debug("interpreted range: " + range);
		
		return range;
	}


	private void printRequestHeader(Request request){
        logger.debug("Request Header:");
		for (String header : request.getHeaderNames()){
            String headerPart = "> ";
			for (String headerValue : request.getHeaders(header)) {
                headerPart += headerValue + " ; ";
			};
            logger.debug(headerPart);
		}
	}

	/**
	 * Send the given range from the given resource as stream
	 * @param response
	 * @param resource The resource to stream
	 * @param range The range of the resource to stream
	 * @param gzip Enable / Disable gzip compression of the stream
	 */
	private void sendStream(final Response response, final ResourceLocation resource, HttpByteRange range, boolean gzip) {
		InputStream inputStream = resource.openInputStream();
		OutputStream outputStream = null;

		try {
			if(gzip)
				response.addHeader(Header.ContentEncoding, "gzip");			
			response.flush(); // Flush the response header.
			
			if(inputStream instanceof ISeekableInputStream)
				((ISeekableInputStream) inputStream).seek(range.from);
			
			outputStream = response.getOutputStream();
			if(gzip){
				outputStream = new GZIPOutputStream(outputStream);
			}
			
			// Stream the file content
			IOUtils.copy(inputStream, outputStream);
		} catch (IOException e) {
            logger.error("Sending stream failed with:" + e.getMessage());
		}finally{
			
			response.finish();
			
			try {
				outputStream.close();
			} catch (IOException e1) {
                logger.error(e1);
			}
			try {
				inputStream.close();
			} catch (IOException e) {
                logger.error(e);
			}
		}
	}


}
