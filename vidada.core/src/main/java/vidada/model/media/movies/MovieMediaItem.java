package vidada.model.media.movies;

import java.beans.Transient;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.settings.GlobalSettings;
import vidada.model.video.Video;
import vidada.model.video.VideoInfo;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.threading.TaskResult;

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
		extractPreferedFrameCached(GlobalSettings.getMaxThumbResolution());
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
		extractPreferedFrameCached(GlobalSettings.getMaxThumbResolution());

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
		return thumbCreationFails <= MAX_THUMB_RETRY_COUT 
				// ensure we do not
				// try forever when
				// the movie is
				// defect
				&& Video.isGenericEncoderPresent() && isAvailable();
	}


	private final Lock thumbnailCreatorLock = new ReentrantLock();

	/**
	 * Creates a thumbnail of the requested size and stores it in the cache. This method may block the
	 * current thread for some time!
	 */
	@Override
	public  void createThumbnailCached(Size size) {
		TaskResult success = TaskResult.Failed;

		synchronized (thumbnailCreatorLock) {
			extractPreferedFrameCached(size);
		}

		//return success;
	}

	@Override
	public void resolveResolution(){
		if(!hasResolution()){
			Video myVideo = getVideo();
			if(myVideo != null){
				VideoInfo info = myVideo.getVideoInfo();
				if(info != null)
					setResolution(info.NativeResolution);
			}
		}
	}


	/**
	 * Extract a native frame from this movie.
	 * The extracted frame is cached upon success.
	 * 
	 * @return The new extracted frame (or null upon failure)
	 */
	private IMemoryImage extractPreferedFrameCached(Size size) {
		IMemoryImage frame = null;

		// first, remove old cached images
		imageService.removeImage(this);

		float pos;

		if (getPreferredThumbPosition() != INVALID_POSITION) {
			pos = getPreferredThumbPosition();
		} else if (getCurrentThumbPosition() != INVALID_POSITION) {
			pos = getCurrentThumbPosition();
		} else {
			pos = getRandomThumbpos();
		}

		frame = extractNativeFrameAt(pos, size);

		if(frame != null){
			System.out.println("native framw extracted successfull. storing it in the image cache...");
			imageService.storeImage(this, frame);
		}else {
			System.err.println("extractNativeSizeFrame: Extracting native frame failed (frame = NULL)");
		}

		return frame;
	}

	/**
	 * Get a frame from this media, at a random position. The frame is returned
	 * in its native size from the video
	 */
	private float getRandomThumbpos() {
		return (float) Math.random();
	}

	/**
	 * Create a thumb at the given position.
	 * 
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	private IMemoryImage extractNativeFrameAt(float position, Size size) {

		Video video = getVideo();

		IMemoryImage frame = video.getFrame(position, size);
		if (frame != null) {
			resolveResolution();
			setCurrentThumbPosition(position);
			thumbCreationFails = 0;
		}else {
			// the thumb could not be generated
			setThumbCreationFails(getThumbCreationFails() + 1);
		}
		persist();

		return frame;
	}

}
