package vidada.model.images;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.LoadPriority;

public class ImageContainerBase implements ImageContainer, Runnable {

	// final
	transient private final ExecutorService imageLoaderPool;
	transient private final Lock imageLoaderLock = new ReentrantLock();
	transient private final Object requestImageLock = new Object();

	// mutable
	transient private volatile IMemoryImage rawImage; // since we don't use any locks, this ensures visibility to another thread
	transient private Callable<IMemoryImage> imageLoader;
	transient private Future<?> imageLoaderTask = null;
	transient private LoadPriority priority = LoadPriority.High;


	// Events

	private EventHandlerEx<EventArgs> ImageChangedEvent = new EventHandlerEx<EventArgs>();
	@Override
	public IEvent<EventArgs> getImageChangedEvent() { return ImageChangedEvent; }


	/**
	 * 
	 * @param imageLoaderPool The context in which the image should be loaded
	 * @param imageLoader The Image loader task (which will be called async) 
	 * @param changedCallback Callback event when the image has changed
	 */
	public ImageContainerBase(ExecutorService imageLoaderPool, Callable<IMemoryImage> imageLoader){
		this.imageLoaderPool = imageLoaderPool;
		this.imageLoader = imageLoader;
	}


	/**
	 * Requests that the given image is being loaded
	 * This method returns immediately
	 */
	@Override
	public void requestImage() {
		requestImage(LoadPriority.High);
	}

	public void requestImage(LoadPriority priority) {
		synchronized (requestImageLock) {
			if(isImageLoaded()) return;
			if(imageLoaderTask == null){
				setLoadPriority(priority);
				imageLoaderTask = imageLoaderPool.submit(this);
			}else {
				// already a task running...
			}
		}
	}

	/**
	 * Load the image in the callers thread
	 * (Thus, should be called asynchronously)
	 */
	private void loadImageSync(){

		if(imageLoaderLock.tryLock()){
			try{
				if(imageLoader == null) return;
				if(isImageLoaded()) return;
				if(LoadPriority.Skip.equals(priority)) return; // just skip this

				try {
					IMemoryImage loadedImage = imageLoader.call();
					setRawImage(loadedImage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}finally{
				imageLoaderLock.unlock();
			}
		}
		// if another thread is already processing this image
		// we can just skip here
	}

	/**
	 * This Callables entry point
	 * --> invokes loadImageSync
	 */
	@Override
	public void run() {
		loadImageSync();
		imageLoaderTask = null;
	}

	@Override
	public IMemoryImage getRawImage() {
		return rawImage;
	}


	@Override
	public boolean isImageLoaded() {
		return getRawImage() != null;
	}


	/**
	 * Informs this container that the current linked image is no longer valid
	 */
	@Override
	public void notifyImageChanged() {
		this.rawImage = null;
		requestImage();
	}


	protected void setRawImage(IMemoryImage rawImage){
		this.rawImage = rawImage;
		onImageChanged();
	}



	/**
	 * Occurs when the image has been loaded
	 */
	protected void onImageChanged(){
		ImageChangedEvent.fireEvent(this, EventArgs.Empty);
	}


	@Override
	public void awaitImage() {
		if(!isImageLoaded()){
			requestImage();
			Future<?> loader = imageLoaderTask;

			try {
				if(loader != null)
					loader.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
	}


	@Override
	public void setLoadPriority(LoadPriority priority) {
		if(!this.priority.equals(priority)){
			this.priority = priority;
			switch (priority) {
			case Skip:

				break;

			case Idle:
				if(!isImageLoaded())
					requestImage();
				break;

			case High:
				if(!isImageLoaded())
					requestImage();
				break;

			default:
				break;
			}
		}
	}


}
