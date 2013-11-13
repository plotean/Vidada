package vidada.images;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.swing.images.MemoryImageAwt;

public class RawImageFactoryAwt implements IRawImageFactory {

	public RawImageFactoryAwt(){

	}


	@Override
	public IMemoryImage createImage(InputStream inputStream) {
		try {

			BufferedImage image = ImageIO.read(inputStream);
			image = toOptimalThumbBitmap(image);

			return new MemoryImageAwt(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage toOptimalThumbBitmap(BufferedImage bitmap){

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();


		BufferedImage optimal = config.createCompatibleImage(
				bitmap.getWidth(),
				bitmap.getHeight(),
				bitmap.getTransparency());

		Graphics g = optimal.getGraphics();
		g.drawImage(bitmap, 0,0, null);
		g.dispose();

		return optimal;
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

	@Override
	public IMemoryImage createImage(URI uri) {
		try {
			return new MemoryImageAwt(ImageIO.read(uri.toURL()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
