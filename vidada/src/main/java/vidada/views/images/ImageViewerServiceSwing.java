package vidada.views.images;

import javax.swing.JDialog;

import vidada.views.mediabrowsers.imageviewer.ImageViewerDialog;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.images.viewer.IImageProvider;
import archimedesJ.images.viewer.IImageViewerService;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.images.viewer.StaticImageProvider;

public class ImageViewerServiceSwing implements IImageViewerService {

	@Override
	public void showImages(IImageProvider imageProvider) {
		throw new NotImplementedException("showImages");
	}

	@Override
	public void showImage(ISmartImage smartImage) {
		JDialog viewerDialog = new ImageViewerDialog(new StaticImageProvider(smartImage));
		viewerDialog.setVisible(true);
	}

}
