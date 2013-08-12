package vidada.model.media.movies;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.net.URI;

import vidada.model.cache.CacheUtils;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.settings.GlobalSettings;
import vidada.model.video.Video;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.swing.TaskResult;
import archimedesJ.util.images.ScalrEx;

/**
 * Represents a playable MoviePart. A full Movie can be composed of multiple
 * MovieParts
 * 
 * @author IsNull
 * 
 */
public class MovieMediaItem extends MediaItem implements Cloneable {

	transient public final static int INVALID_POSITION = -1;

	transient private final static int MAX_THUMB_RETRY_COUT = 2;

	transient private Video video;

	private float preferredThumbPosition = INVALID_POSITION;
	private float currentThumbPosition = INVALID_POSITION;
	private volatile int thumbCreationFails = 0;

	/**
	 * hibernate constructor
	 */
	public MovieMediaItem() {
	}

	/**
	 * Creates a new MoviePart
	 * 
	 * @param parentLibrary
	 * @param movieFile
	 * @param hash
	 */
	public MovieMediaItem(MediaLibrary parentLibrary, URI relativePath, String hash) {
		super(parentLibrary, relativePath);
		setFilehash(hash);
		setMediaType(MediaType.MOVIE);
	}

	/**
	 * Copy constructor
	 * 
	 * @param prototype
	 */
	public MovieMediaItem(MovieMediaItem prototype) {
		prototype(prototype);
	}

	public void prototype(MovieMediaItem prototype) {

		setPreferredThumbPosition(prototype.getPreferredThumbPosition());
		setCurrentThumbPosition(prototype.getCurrentThumbPosition());

		super.prototype(prototype);
	}

	public int getThumbCreationFails() {
		return thumbCreationFails;
	}

	protected void setThumbCreationFails(int thumbCreationFails) {
		this.thumbCreationFails = thumbCreationFails;
	}

	/**
	 * Get the preferred thumb position of this movie
	 * 
	 * @return
	 */
	public float getPreferredThumbPosition() {
		return preferredThumbPosition;
	}

	/**
	 * Set the preferred thumb position of this movie
	 * 
	 * @param position
	 */
	public void setPreferredThumbPosition(float position) {
		this.preferredThumbPosition = position;
	}

	/**
	 * Get the current position of the thumb
	 * 
	 * @return
	 */
	public float getCurrentThumbPosition() {
		return currentThumbPosition;
	}

	/**
	 * Set the current position of the thumb
	 * 
	 * @param position
	 */
	public void setCurrentThumbPosition(float position) {
		this.currentThumbPosition = position;
	}

	/**
	 * Sets the current used thumb as preferred one
	 */
	public void setCurrentThumbAsPreferred() {
		setPreferredThumbPosition(getCurrentThumbPosition());
	}

	/**
	 * Create a new random thumb
	 * 
	 * This will clear the current preferred thumb and replace it with the new
	 * one
	 */
	public void createNewRandomThumb() {
		setCurrentThumbPosition(INVALID_POSITION);
		setPreferredThumbPosition(INVALID_POSITION);
		createNewCachedThumb();
		setCurrentThumbAsPreferred();

		this.persist();
	}

	/**
	 * Create a new cached thumb at the given position
	 * 
	 * @param position
	 */
	public void createNewCachedThumb(float position) {
		setPreferredThumbPosition(position);
		createNewCachedThumb();

		this.persist();
	}

	/**
	 * Get the native video
	 * 
	 * @return
	 */
	@Transient
	public Video getVideo() {

		MediaSource source = getSource();
		if(source != null && source.isAvailable())
		{
			if(source instanceof FileMediaSource)
			{
				FileMediaSource fileSource = (FileMediaSource)source;
				if (video == null) {
					video = new Video(fileSource.getAbsoluteFilePath());
				} else
					video.setPathToVideoFile(fileSource.getAbsoluteFilePath());
			}else
				throw new NotSupportedException("Can not retrive video from this Source: " + source);
		}

		return video;
	}

	/**
	 * Is it possible to create a thumb of this movie part?
	 * 
	 * Generally, the file must be available and a video encoder for this format
	 * must be present as well.
	 * 
	 */
	@Override
	@Transient
	public boolean canCreateThumbnail() {
		return thumbCreationFails <= MAX_THUMB_RETRY_COUT // ensure we do not
				// try forever when
				// the movie is
				// defect
				&& Video.isGenericEncoderPresent() && isAvailable();
	}

	/**
	 * Creates a thumbnail of the requested size. This method may block the
	 * current thread for some time!
	 */
	@Override
	public TaskResult createCachedThumbnail(Size size) {
		TaskResult success = TaskResult.Failed;
		synchronized (thumbnailCreatorLock) {
			if (isCreatingThumbnail)
				throw new NotSupportedException("Alread creating a thumbnail! Check isCreatingThumbnail()");
			isCreatingThumbnail = true;
		}

		try {

			BufferedImage thumb = CacheUtils.getRescaledInstance(imageCache, getThumbId(), size);

			if (thumb != null) {
				imageCache.storeImage(getFilehash(), thumb);
				success = TaskResult.Completed;
			} else {
				// the cache does not have a usefull image
				isCreatingThumbnail = false;
				createNewCachedThumb();
				success = TaskResult.Completed;
				//success = createCachedThumbnail(size); // recourse into
			}

		} finally {
			synchronized (thumbnailCreatorLock) {
				isCreatingThumbnail = false;
			}
		}
		return success;
	}

	/**
	 * Creates the thumb and stores it in the cache
	 * 
	 */
	public boolean createNewCachedThumb() {

		boolean success = false;

		synchronized (thumbnailCreatorLock) {
			if (isCreatingThumbnail)
				throw new NotSupportedException("Alread creating a thumbnail! Check isCreatingThumbnail()");
			isCreatingThumbnail = true;
		}

		try {
			System.out.println("force creating new thumb of -> " + this.getFilename());
			BufferedImage frame = extractNativeSizeFrameCached();
			success = frame != null;
			persist();
			firePropertyChange("cachedThumbnail");
		} finally {
			synchronized (thumbnailCreatorLock) {
				isCreatingThumbnail = false;
			}
		}

		return success;
	}

	/**
	 * Get the current native sized preview frame for this video
	 * If there is no frame cached, a new one will be created.
	 * @return
	 */
	@Transient
	public Image getCurrentNativeFrame() {
		Image frame = extractRandomNativeSizeFrame();
		if (frame == null)
			frame = extractNativeSizeFrameCached();
		return frame;
	}

	@Override
	public void resolveResolution(){
		Size nativeResolution = imageCache.getNativeImageResolution(getThumbId());
		setResolution(nativeResolution);
	}


	/**
	 * Extract a native frame from this movie.
	 * The extracted frame is cached upon success.
	 * 
	 * @return The new extracted frame (or null upon failure)
	 */
	@Transient
	protected BufferedImage extractNativeSizeFrameCached() {
		BufferedImage frame = null;

		// first, remove old cached images
		imageCache.removeImage(getThumbId()); 

		if (getPreferredThumbPosition() != INVALID_POSITION) {
			frame = extractNativeFrameAt(getPreferredThumbPosition());
		} else if (getCurrentThumbPosition() != INVALID_POSITION) {
			frame = extractNativeFrameAt(getCurrentThumbPosition());
		} else {
			frame = extractRandomNativeSizeFrame();
		}

		if(frame != null){
			System.out.println("native framw extracted successfull. storing it in the image cache...");
			imageCache.storeNativeImage(getFilehash(), frame);

			if(frame != null){
				Size maxThumb = GlobalSettings.getMaxThumbResolution();
				BufferedImage thumb = ScalrEx.rescaleImage(frame, maxThumb.width, maxThumb.height);
				imageCache.storeImage(getFilehash(), thumb);
			}
		}else {
			System.err.println("extractNativeSizeFrame: Extracting native frame failed (frame = NULL)");
		}

		return frame;
	}

	/**
	 * Get a frame from this media, at a random position. The frame is returned
	 * in its native size from the video
	 */
	@Transient
	protected BufferedImage extractRandomNativeSizeFrame() {
		return extractNativeFrameAt((float) Math.random());
	}

	/**
	 * Create a thumb at the given position.
	 * 
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	@Transient
	protected BufferedImage extractNativeFrameAt(float position) {
		BufferedImage frame = getVideo().getNativeFrame(position);
		if (frame == null)
			onThumbCreationFailed();
		else {
			if(!hasResolution())
				setResolution(new Size(frame.getWidth(), frame.getHeight()));
			setCurrentThumbPosition(position);
			thumbCreationFails = 0;
		}
		return frame;
	}

	/**
	 * Occurs when a thumb could not be created
	 */
	private void onThumbCreationFailed() {
		setThumbCreationFails(getThumbCreationFails() + 1);
		persist();
	}
}
