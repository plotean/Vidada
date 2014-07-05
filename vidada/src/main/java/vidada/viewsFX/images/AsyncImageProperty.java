package vidada.viewsFX.images;


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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class AsyncImageProperty extends SimpleObjectProperty<Image> {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(AsyncImageProperty.class.getName());

    private final ImageLoadService imageLoadService = new ImageLoadService();
	private final ObjectProperty<ImageContainer> imageContainer = new SimpleObjectProperty<ImageContainer>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new AsyncImageProperty
     */
	public AsyncImageProperty() {
		imageLoadService.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State value) {
				if(value == State.SUCCEEDED) {
					set(imageLoadService.getValue());
				}else if(value == State.FAILED) {
					set(null);
                    logger.warn("ImageLoadService failed to load image.");
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

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * The {@link ImageContainer} which is responsible for loading a image
     * @return
     */
	public ObjectProperty<ImageContainer> imageContainerProperty() {
		return imageContainer;
	}

    /**
     * Set the image container
     * @param imageContainer
     */
    public void setImageContainer(ImageContainer imageContainer){
        this.imageContainer.set(imageContainer);
    }

    /**
     * Get the image container
     * @return
     */
    public ImageContainer getImageContainer(){
        return this.imageContainer.get();
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private void updateImage(){
        if(imageLoadService.isRunning()) {
            imageLoadService.cancel();
        }
        loadImageInBackground();
    }

	private EventListenerEx<EventArgs> imageContainerImageChanged = (sender, eventArgs) -> {
        final ImageContainer container = imageContainer.get();
        if(!container.isImageLoaded()){
            updateImage();
        }
    };

	private void loadImageInBackground() {
		synchronized(imageLoadService) {
			if(imageContainer != null) {
				imageLoadService.restart();
			}
		}
	}

    /***************************************************************************
     *                                                                         *
     * Inner classes                                                           *
     *                                                                         *
     **************************************************************************/

    /**
     * Async image load task
     */
	private class ImageLoadService extends Service<Image> {

		@Override
		protected Task<Image> createTask() {

			return new Task<Image>() {
				@Override
				protected Image call() {

					final ImageContainer container = imageContainer.getValue();

                    if(container != null){
                        container.requestImage();
                        container.awaitImage();

                        IMemoryImage image = container.getRawImage();

                        if(image != null){
                            if(image.getOriginal() instanceof Image){
                                return (Image)image.getOriginal();
                            }else{
                                logger.error("Received image of wrong type: " + image.getOriginal());
                            }
                        }
                    }
					return null;
				}
			};
		}
	}
}
