package vidada.model.media.movies;

import java.beans.Transient;
import java.net.URI;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.video.Video;
import vidada.model.video.VideoInfo;
import archimedesJ.io.locations.ResourceLocation;

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
	//transient private final Lock thumbnailCreatorLock = new ReentrantLock();


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

	public void onThumbCreationFailed(){
		// increment failure counter
		setThumbCreationFails(getThumbCreationFails()+1);
	}

	public void onThumbCreationSuccess(){
		setThumbCreationFails(0);
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

	public void createNewRandomThumb() {
		setCurrentThumbPosition(INVALID_POSITION);
		setPreferredThumbPosition(INVALID_POSITION);
		extractPreferedFrameCached(GlobalSettings.getMaxThumbResolution());
		setCurrentThumbAsPreferred();

		this.persist();
	} */

	/**
	 * Create a new cached thumb at the given position
	 * 
	 * @param position

	public void createNewCachedThumb(float position) {
		setPreferredThumbPosition(position);
		extractPreferedFrameCached(GlobalSettings.getMaxThumbResolution());

		this.persist();
	} */

	/**
	 * Get the native video
	 * 
	 * @return
	 */
	@Transient
	public Video getVideo() {
		IMediaSource source = getSource();
		if(source != null && source.isAvailable())
		{
			ResourceLocation path = source.getResourceLocation();

			if (video == null && path != null) {
				video = new Video(path);
			} else
				video.setPathToVideoFile(path);
		}
		return video;
	}

	/**
	 * Is it possible to create a thumb of this movie part?
	 * 
	 * Generally, the file must be available and a video encoder for this format
	 * must be present, lastly previous fails must be below <code>MAX_THUMB_RETRY_COUT</code>
	 * 
	 */
	public boolean canCreateThumbnail() {
		// Ensure we do not try forever when we deal with defect viedeos
		return thumbCreationFails <= MAX_THUMB_RETRY_COUT  && isAvailable();
	}

	public float getThumbPos(){
		float pos;
		if (getPreferredThumbPosition() != MovieMediaItem.INVALID_POSITION) {
			pos = getPreferredThumbPosition();
		} else if (getCurrentThumbPosition() != MovieMediaItem.INVALID_POSITION) {
			pos = getCurrentThumbPosition();
		} else {
			pos = (float)Math.random();
		}
		return pos;
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



}
