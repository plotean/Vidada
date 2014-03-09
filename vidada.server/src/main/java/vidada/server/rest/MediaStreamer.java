package vidada.server.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class MediaStreamer implements StreamingOutput {

	private int length;
	private InputStream randomAccess;
	final byte[] buf = new byte[4096];

	public MediaStreamer(int length, InputStream randomAccess) {
		this.length = length;
		this.randomAccess = randomAccess;
	}

	@Override
	public void write(OutputStream outputStream) throws IOException, WebApplicationException {
		try {
			while( length != 0) {
				int read = randomAccess.read(buf, 0, buf.length > length ? length : buf.length);
				System.out.println("writing " + read + " bytes to output stream.");
				outputStream.write(buf, 0, read);
				length -= read;
			}
		} finally {
			randomAccess.close();
		}
	}

	public int getLenth() {
		return length;
	}
}
