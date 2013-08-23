package vidada.views.mediabrowsers.imageviewer;

import java.awt.Image;

import vidada.model.media.movies.MovieMediaItem;
import vidada.model.settings.GlobalSettings;
import archimedesJ.images.ImageContainer;
import archimedesJ.swing.components.imageviewer.ISmartImage;

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
		Image image = null;

		ImageContainer container = videoPart.getThumbnail(GlobalSettings.getMaxThumbResolution());

		if(!container.isImageLoaded()){
			container.awaitImage();
		}

		image = (Image)container.getRawImage();

		return image;
	}

}
