package vidada.views.mediabrowsers.imageviewer;

import java.awt.Image;

import vidada.model.media.movies.MovieMediaItem;
import archimedesJ.swing.components.imageviewer.ISmartImage;
import archimedesJ.swing.images.AsyncImage;

public class MoviePartWrapper implements ISmartImage {

	private MovieMediaItem videoPart;

	public MoviePartWrapper(MovieMediaItem videoPart) {
		this.videoPart = videoPart;
	}

	@Override
	public String getTitle() {
		return videoPart.getTitle();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Image getImage() {
		Image image = videoPart.getCurrentNativeFrame();
		// System.out.println("MoviePartWrapper:getImage " + image);

		if (image instanceof AsyncImage<?>) {
			image = ((AsyncImage) image).waitForImage();
		}

		return image;
	}

}
