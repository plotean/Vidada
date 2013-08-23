package vidada.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import vidada.model.images.RawImageFactory;
import archimedesJ.images.IMemoryImage;

public class RawImageFactoryAwt implements RawImageFactory {

	@Override
	public IMemoryImage createImage(InputStream inputStream) {
		try {
			return new MemoryImageAwt(ImageIO.read(inputStream));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean writeImage(IMemoryImage image, OutputStream outputStream) {
		if(!(image.getOriginal() instanceof BufferedImage))
			throw new IllegalArgumentException("image must be of type BufferedImage");
		BufferedImage bufferedImage = (BufferedImage)image.getOriginal();
		try {
			return ImageIO.write(bufferedImage, "png", outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public IMemoryImage createImage(File file) {
		try {
			return new MemoryImageAwt(ImageIO.read(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
