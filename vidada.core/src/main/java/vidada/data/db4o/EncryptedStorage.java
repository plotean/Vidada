package vidada.data.db4o;

import java.io.IOException;

import archimedesJ.crypto.IRandomAccessEncryptionAlgorythm;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.SimpleRandomAccessEncryptionAlgorythm;

import com.db4o.ext.Db4oIOException;
import com.db4o.io.Bin;
import com.db4o.io.BinConfiguration;
import com.db4o.io.FileStorage;
import com.db4o.io.StorageDecorator;

/**
 * 
 * @author IsNull
 *
 */
public class EncryptedStorage extends StorageDecorator {

	private final IRandomAccessEncryptionAlgorythm crypto;

	public EncryptedStorage(String password){
		super(new FileStorage());
		crypto = new SimpleRandomAccessEncryptionAlgorythm(KeyPad.hashKey(password));
	}

	@Override
	public Bin open(BinConfiguration binconf) throws Db4oIOException {
		return super.open(binconf);
		//return new EncryptedBin(_storage.open(binconf), crypto);
	}

	@Override
	public void rename(String arg0, String arg1) throws IOException {
		_storage.rename(arg0, arg1);
	}

	private class EncryptedBin implements Bin
	{
		int globaloffset = 0;
		private final Bin original;
		private final IRandomAccessEncryptionAlgorythm crypto;

		public EncryptedBin(Bin original, IRandomAccessEncryptionAlgorythm crypto){
			this.original = original;
			this.crypto = crypto;
		}

		@Override
		public void close() {
			original.close();
		}



		@Override
		public long length() {
			return original.length()+globaloffset;
		}



		@Override
		public int read(long position, byte[] bytes, int bytesToRead) {
			System.out.println("syncRead: from" + position + " reading bytes:" + bytesToRead + " buf: " + bytes.length);
			int readbytes = original.read(position+globaloffset, bytes, bytesToRead);
			crypto.decrypt(position, bytes, readbytes);
			return readbytes;
		}

		/**
		 * reads a given number of bytes into an array of bytes at an offset position.
		 */
		@Override
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			System.out.println("syncRead: from" + position + " reading bytes:" + bytesToRead + " buf: " + bytes.length);
			int bytesRead = original.syncRead(position+globaloffset, bytes, bytesToRead);
			crypto.decrypt(position, bytes, bytesRead);
			return bytesRead;
		}

		@Override
		public void write(long position, byte[] bytes, int bytesToWrite) {
			crypto.encrypt(position, bytes, bytesToWrite);
			original.write(position+globaloffset, bytes, bytesToWrite);
		}

		/**
		 *  flushes the buffer content to the physical storage media.
		 */
		@Override
		public void sync() {
			original.sync();
		}

		/**
		 * runs the Runnable between two calls to sync();
		 */
		@Override
		public void sync(Runnable delegate) {
			original.sync(delegate);
		}


	}

}
