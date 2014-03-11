package vidada.server.rest.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import vidada.server.rest.MediaStreamer;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.io.streaming.ISeekableInputStream;

public abstract class AbstractStreamResource extends AbstractResource {

	final int chunk_size = 1024 * 1024; // 1MB chunks
	final static int  STATUS_PARTIAL_CONTENT = 206;

	protected Response buildStream(final ResourceLocation asset, final String range) throws Exception {

		final InputStream input = asset.openInputStream();

		// range not requested : Firefox, Opera, IE do not send range headers
		// the file / file-system must support random access
		if (range == null || !(input instanceof ISeekableInputStream)) {
			StreamingOutput streamer = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {

					//final FileChannel inputChannel = new FileInputStream(asset).getChannel();
					//final WritableByteChannel outputChannel = Channels.newChannel(output);

					IOUtils.copy(input, output);
				}
			};
			return Response.ok(streamer)
					.header(HttpHeaders.CONTENT_LENGTH, asset.length())
					.header("Accept-Ranges", "bytes")
					.type(asset.getMimeType())
					.location(null)
					.build();
		}

		System.out.println("range: " + range);

		String[] ranges = range.split("=")[1].split("-");
		final int from = Integer.parseInt(ranges[0]);
		/**
		 * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
		 */
		int to = chunk_size + from;
		if (to >= asset.length()) { 
			to = (int) (asset.length() - 1);
		}
		if (ranges.length == 2) {
			to = Integer.parseInt(ranges[1]);
		}

		final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
		//final RandomAccessFile raf = new RandomAccessFile(asset, "r");
		ISeekableInputStream seekable = (ISeekableInputStream)input;
		seekable.seek(from);

		final int len = to - from + 1;
		final MediaStreamer streamer = new MediaStreamer(len, input);
		Response.ResponseBuilder res = Response.ok(streamer)
				.status(STATUS_PARTIAL_CONTENT)
				.header("Accept-Ranges", "bytes")
				.header("Content-Range", responseRange)
				.header("Content-Type", asset.getMimeType())
				.header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
				; // .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()))
		return res.build();
	}
}
