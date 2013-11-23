package vidada.viewsFX.images;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import vidada.model.images.ImageContainerBase;
import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.viewsFX.mediabrowsers.ImagePane;
import vidada.viewsFX.util.AsyncImageProperty;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.viewer.IImageProvider;
import archimedesJ.images.viewer.ISmartImage;

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

		imageProvider.getCurrentImageChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				updateCurrentImage();	
			}
		});

		btnLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				imageProvider.navigatePrevious();
			}
		});

		btnRight.addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				imageProvider.navigateNext();
			}
		});

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
		ImageContainerBase container = new ImageContainerBase(pool, new Callable<IMemoryImage>() {

			@Override
			public IMemoryImage call() throws Exception {
				return image.getImage();
			}
		}, new ImageChangedCallback(){

			@Override
			public void imageChanged(ImageContainer container) {
				System.out.println("SmartImagePanelFx:ImageContainerBase imageChanged");
			}});

		return container;
	}


}
