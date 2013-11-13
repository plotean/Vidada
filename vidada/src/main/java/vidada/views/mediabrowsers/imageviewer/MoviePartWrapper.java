package vidada.views.mediabrowsers.imageviewer;

import vidada.model.media.movies.MovieMediaItem;
import vidada.model.settings.GlobalSettings;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.viewer.ISmartImage;

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
	public IMemoryImage getImage() {
		ImageContainer container = videoPart.getThumbnail(GlobalSettings.getMaxThumbResolution());

		if(!container.isImageLoaded()){
			container.awaitImage();
		}

		return container.getRawImage();
	}

}
