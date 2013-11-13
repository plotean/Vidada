package vidada.images;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import archimedesJ.images.IMemoryImage;
import archimedesJ.swing.images.ScalrEx;

import com.db4o.foundation.ArgumentNullException;

public class MemoryImageFx implements IMemoryImage {

	private final Image original;

	/**
	 * 
	 * @param original The original image. Must not be null.
	 */
	public MemoryImageFx(Image original){
		if(original == null)
			throw new ArgumentNullException("original");

		this.original = original;
	}

	@Override
	public Image getOriginal() {
		return this.original;
	}

	@Override
	public int getWidth() {
		return (int)this.original.getWidth();
	}

	@Override
	public int getHeight() {
		return (int)this.original.getHeight();
	}

	@Override
	public IMemoryImage rescale(int width, int heigth) {

		BufferedImage img = new BufferedImage(
				(int)original.getWidth(),
				(int)original.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		SwingFXUtils.fromFXImage(original, img);
		BufferedImage rescaled = ScalrEx.rescaleImage(img, width, heigth);
		WritableImage rescaledFX = new WritableImage(width, heigth);

		return new MemoryImageFx(SwingFXUtils.toFXImage(rescaled, rescaledFX));
	}

	@Override
	public String toString(){
		return "MemoryImageFx: org: " + getOriginal() + " (" + getWidth() + "x" + getHeight() +")";
	}

}
