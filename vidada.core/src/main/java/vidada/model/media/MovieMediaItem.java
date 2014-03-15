package vidada.model.media;

import java.net.URI;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

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

	private float preferredThumbPosition = INVALID_POSITION;
	private float currentThumbPosition = INVALID_POSITION;
	private volatile int thumbCreationFails = 0;
	private int bitrate;
	private int duration;

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
		setBitrate(prototype.getBitrate());
		setDuration(prototype.getDuration());

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
			pos = randomRelativePos();
		}
		return pos;
	}

	/**
	 * Returns a random relative position in the movie.
	 * This method ensures that the position is not too near 
	 * of the start or end of the movie. 
	 * 
	 * (Lots of movies have a boring screener at the start
	 * and even more boring credits listing at the end.)
	 * 
	 * @return
	 */
	public static final float randomRelativePos(){
		float pos = (float)Math.random();
		pos = Math.max(pos, 0.08f);
		pos = Math.min(pos, 0.90f);
		return pos;
	}

	/**
	 * Set the bitrate in Kilo bits (Kb)
	 * @param bitRate
	 */
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * Gets the bitrate in Kilo bits per second
	 * @return
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * Gets the duration in seconds
	 * @return
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration in seconds
	 * @param duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}


	@Override
	public MovieMediaItem clone() {
		return new MovieMediaItem(this);
	}
}
