package vidada.viewsFX.images;

import archimedes.core.images.viewer.IImageProvider;
import archimedes.core.images.viewer.IImageViewerService;
import archimedes.core.images.viewer.ISmartImage;
import archimedes.core.images.viewer.StaticImageProvider;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerServiceFx implements IImageViewerService {

	@Override
	public void showImages(IImageProvider imageProvider) {

		SmartImagePanelFx panel = new SmartImagePanelFx(imageProvider);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
		Scene scene = new Scene(panel);
        stage.setScene(scene);
        stage.show();
	}

	@Override
	public void showImage(ISmartImage image) {
		showImages(new StaticImageProvider(image));
	}

}
