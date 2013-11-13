package vidada.images;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;

public class RawImageFactoryFx implements IRawImageFactory {

	public RawImageFactoryFx(){

	}


	@Override
	public IMemoryImage createImage(InputStream inputStream) {
		Image imageFx = new Image(inputStream);
		return new MemoryImageFx(imageFx);
	}

	@Override
	public boolean writeImage(IMemoryImage image, OutputStream outputStream) {
		if(!(image.getOriginal() instanceof Image))
			throw new IllegalArgumentException("image must be of type javafx.*.Image");

		Image imageFX = (Image)image.getOriginal();

		try {
			return ImageIO.write(SwingFXUtils.fromFXImage(imageFX, null), "png", outputStream);
		} catch (Exception s) {
			s.printStackTrace();
		}

		return false;
	}

	@Override
	public IMemoryImage createImage(File file) {
		if(file.exists()){
			return createImage(file.toURI());
		}else{
			System.err.println("createImage failed: " + file + "(missing)");
		}
		return null;
	}

	@Override
	public IMemoryImage createImage(URI file) {
		return new MemoryImageFx(new Image(file.toString()));
	}

}
