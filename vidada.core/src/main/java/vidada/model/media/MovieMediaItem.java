package vidada.model.media;

import java.net.URI;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import vidada.model.video.Video;

/**
 * Represents a playable MoviePart. A full Movie can be composed of multiple
 * MovieParts
 * 
 * @author IsNull
 * 
 */
@Entity
@XmlRootElement
public class MovieMediaItem extends MediaItem implements Cloneable {

	transient public final static int INVALID_POSITION = -1;
	transient private final static int MAX_THUMB_RETRY_COUT = 2;
	transient private Video video;
	//transient private final Lock thumbnailCreatorLock = new ReentrantLock();


	private float preferredThumbPosition = INVALID_POSITION;
	private float currentThumbPosition = INVALID_POSITION;
	private volatile int thumbCreationFails = 0;

	/**
	 * ORM constructor
	 */
	MovieMediaItem() {
		classinfo = "movie";
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
		setType(MediaType.MOVIE);
		classinfo = "movie";
	}

	/**
	 * Copy constructor
	 * 
	 * @param prototype
	 */
	public MovieMediaItem(MovieMediaItem prototype) {
		this();
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
	 * Is it possible to create a thumb of this movie part?
	 * 
	 * Generally, the file must be available and a video encoder for this format
	 * must be present, lastly previous fails must be below <code>MAX_THUMB_RETRY_COUT</code>
	 * 
	 */
	public boolean canCreateThumbnail() {
		// Ensure we do not try forever when we deal with defect videos
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
}
