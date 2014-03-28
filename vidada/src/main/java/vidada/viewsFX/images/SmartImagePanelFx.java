package vidada.viewsFX.images;

import archimedes.core.images.ImageContainer;
import archimedes.core.images.ImageContainerBase;
import archimedes.core.images.viewer.IImageProvider;
import archimedes.core.images.viewer.ISmartImage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import vidada.viewsFX.mediabrowsers.ImagePane;
import vidada.viewsFX.util.AsyncImageProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartImagePanelFx extends BorderPane {

	private final IImageProvider imageProvider;
	private final AsyncImageProperty imageProperty = new AsyncImageProperty();
	private final ImagePane imagePane = new ImagePane(imageProperty);
	private final Label lblName = new Label("Image Name");

	private final Button btnRight = new Button(">");
	private final Button btnLeft = new Button("<");

	private ExecutorService pool = Executors.newFixedThreadPool(1);

	public SmartImagePanelFx(final IImageProvider imageProvider){
		this.imageProvider = imageProvider;

		imagePane.setPreserveRatio(true);

		this.setCenter(imagePane);
		this.setBottom(lblName);

		BorderPane.setAlignment(btnLeft, Pos.CENTER);
		BorderPane.setAlignment(btnRight, Pos.CENTER);
		this.setLeft(btnLeft);
		this.setRight(btnRight);

		imageProvider.getCurrentImageChanged().add((sender, eventArgs) -> updateCurrentImage());

		btnLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, me -> imageProvider.navigatePrevious());

		btnRight.addEventHandler(MouseEvent.MOUSE_CLICKED, me -> imageProvider.navigateNext());

		updateCurrentImage();
	}

	private void updateCurrentImage(){
		if(imageProvider.currentImage() != null){
			imageProperty.imageContainerProperty().set(getContainer(imageProvider.currentImage()));
		}else{
			imageProperty.imageContainerProperty().set(null);
		}
	}

	private ImageContainer getContainer(final ISmartImage image){
		ImageContainerBase container = new ImageContainerBase(pool, () -> image.getImage());

		return container;
	}


}
