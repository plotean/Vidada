package vidada.viewsFX.util;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.scene.image.Image;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.ImageContainer;

public class AsyncImageProperty extends SimpleObjectProperty<Image> {
	private final ImageLoadService imageLoadService = new ImageLoadService();
	private final ObjectProperty<ImageContainer> imageContainer = new SimpleObjectProperty<ImageContainer>();

	public AsyncImageProperty() {
		imageLoadService.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State value) {
				if(value == State.SUCCEEDED) {
					set(imageLoadService.getValue());
				}else if(value == State.FAILED) {
					set(null);
					System.err.println("AsyncImageProperty.imageLoadService: image failed to load.");
				}else if(value == State.CANCELLED) {
					// loading has been canceled
					set(null);
				}
			}
		});

		imageContainer.addListener(new ChangeListener<ImageContainer>() {
			@Override
			public void changed(ObservableValue<? extends ImageContainer> observable, ImageContainer oldValue, ImageContainer value) {

				if(oldValue != null) oldValue.getImageChangedEvent().remove(imageContainerImageChanged);

				updateImage();

				if(value != null) value.getImageChangedEvent().add(imageContainerImageChanged);
			}
		});
	}

	public ObjectProperty<ImageContainer> imageContainerProperty() {
		return imageContainer;
	}

	private void updateImage(){

		if(imageLoadService.isRunning()) {
			imageLoadService.cancel();
		}

		loadImageInBackground();
	}


	private EventListenerEx<EventArgs> imageContainerImageChanged = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			final ImageContainer container = imageContainer.get(); 
			if(!container.isImageLoaded()){
				updateImage();
			}
		}
	};

	private void loadImageInBackground() {
		synchronized(imageLoadService) {
			if(imageContainer != null) {
				imageLoadService.restart();
			}
		}
	}

	private class ImageLoadService extends Service<Image> {

		@Override
		protected Task<Image> createTask() {

			return new Task<Image>() {
				@Override
				protected Image call() {

					final ImageContainer container = imageContainer.getValue();

					container.requestImage();
					container.awaitImage();

					IMemoryImage image = container.getRawImage();

					if(image != null){
						if(image.getOriginal() instanceof Image){
							return (Image)image.getOriginal();
						}else{
							System.err.println("recived image of wrong type: " + image.getOriginal());
						}
					}
					return null;
				}
			};
		}
	}
}
