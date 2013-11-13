package vidada.viewsFX.images;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import archimedesJ.images.viewer.IImageProvider;
import archimedesJ.images.viewer.IImageViewerService;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.images.viewer.StaticImageProvider;

public class ImageViewerServiceFx implements IImageViewerService {

	@Override
	public void showImages(IImageProvider imageProvider) {

		SmartImagePanelFx panel = new SmartImagePanelFx(imageProvider);

		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Scene scene = new Scene(panel);
		dialog.setScene(scene);
		dialog.show();
	}

	@Override
	public void showImage(ISmartImage image) {
		showImages(new StaticImageProvider(image));
	}

}
