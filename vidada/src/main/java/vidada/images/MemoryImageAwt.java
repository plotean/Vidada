package vidada.images;

import java.awt.image.BufferedImage;

import archimedesJ.images.IMemoryImage;
import archimedesJ.swing.images.ScalrEx;

import com.db4o.foundation.ArgumentNullException;

public class MemoryImageAwt implements IMemoryImage {

	private final BufferedImage original;

	/**
	 * 
	 * @param original The original image. Must not be null.
	 */
	public MemoryImageAwt(BufferedImage original){
		if(original == null)
			throw new ArgumentNullException("original");

		this.original = original;
	}

	@Override
	public BufferedImage getOriginal() {
		return this.original;
	}

	@Override
	public int getWidth() {
		return this.original.getWidth();
	}

	@Override
	public int getHeight() {
		return this.original.getHeight();
	}

	@Override
	public IMemoryImage rescale(int width, int heigth) {
		BufferedImage rescaled = ScalrEx.rescaleImage(getOriginal(), width, heigth);
		return new MemoryImageAwt(rescaled);
	}

}
