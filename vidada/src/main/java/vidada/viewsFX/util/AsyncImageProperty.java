package vidada.viewsFX.util;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.scene.image.Image;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

public class AsyncImageProperty extends SimpleObjectProperty<Image> {
	private final ImageLoadService imageLoadService = new ImageLoadService();
	private final ObjectProperty<ImageContainer> imageContainer = new SimpleObjectProperty<ImageContainer>();

	public AsyncImageProperty() {
		imageLoadService.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State value) {
				if(value == State.SUCCEEDED) {
					set(imageLoadService.getValue());
					//System.out.println("success image loaded");
				}
				if(value == State.FAILED) {
					set(null);
					System.err.println("failed image not loaded");
				}
				if(value == State.SUCCEEDED || value == State.CANCELLED || value == State.FAILED) {
					ImageContainer handle = imageContainer.get();
					if(handle != null && !handle.equals(imageLoadService.container)) {
						loadImageInBackground(handle);
					}
				}
			}
		});

		imageContainer.addListener(new ChangeListener<ImageContainer>() {
			@Override
			public void changed(ObservableValue<? extends ImageContainer> observable, ImageContainer oldValue, ImageContainer value) {
				if(!imageLoadService.isRunning()) {
					loadImageInBackground(imageContainer.getValue());
				}
			}
		});
	}

	public ObjectProperty<ImageContainer> imageUrlProperty() {
		return imageContainer;
	}

	private void loadImageInBackground(ImageContainer imageContainer) {
		synchronized(imageLoadService) {
			if(imageContainer != null) {
				imageLoadService.setImageContainer(imageContainer);
				imageLoadService.restart();
			}
		}
	}

	private static class ImageLoadService extends Service<Image> {
		private ImageContainer container;

		public void setImageContainer(ImageContainer container) {
			this.container = container;
		}

		@Override
		protected Task<Image> createTask() {
			final ImageContainer container = this.container;

			return new Task<Image>() {
				@Override
				protected Image call() {

					container.requestImage();
					container.awaitImage();

					IMemoryImage image = container.getRawImage();
					if(image.getOriginal() instanceof Image){
						return (Image)image.getOriginal();
					}else{
						System.err.println("recived image of wrong type: " + image.getOriginal());
					}

					return null;
				}
			};
		}
	}
}
