package vidada.model.images;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import archimedesJ.images.IMemoryImage;
import archimedesJ.services.IService;

public interface RawImageFactory extends IService {


	/**
	 * Creates a raw memory bitmap from the file path
	 * @param filePath
	 * @return
	 */
	public abstract IMemoryImage createImage(File file);

	/**
	 * Creates a raw memory bitmap from the input stream
	 * @param filePath
	 * @return
	 */
	public abstract IMemoryImage createImage(InputStream inputStream);

	/**
	 * Writes the given image into a new file
	 * @param image
	 * @param filePath
	 * @return
	 */
	public abstract boolean writeImage(IMemoryImage image, OutputStream outputStream);
}
