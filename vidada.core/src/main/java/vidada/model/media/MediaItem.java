package vidada.model.media;

import archimedes.core.data.observable.IObservableCollection;
import archimedes.core.data.observable.ObservableCollection;
import archimedes.core.geometry.Size;
import archimedes.core.util.Lists;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;
import vidada.model.entities.BaseEntity;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.tags.Tag;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.beans.Transient;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single media item
 * 
 * @author IsNull
 * 
 */
@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "classinfo")  
@JsonSubTypes({  
	@Type(value = MovieMediaItem.class, name = "movie"),  
	@Type(value = ImageMediaItem.class, name = "image") })  
@XmlRootElement
@Entity
public abstract class MediaItem extends BaseEntity {

	/***************************************************************************
	 *                                                                         *
	 * Private persistent fields                                               *
	 *                                                                         *
	 **************************************************************************/

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<MediaSource> sources = new HashSet<MediaSource>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH }) //cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE}
	private Set<Tag> tags = new HashSet<Tag>();

	@Id
	private String filehash = null;
	private String filename = null;
	private DateTime addedDate = new DateTime();
    private long fileSize = -1;
	private Size resolution = Size.Empty;
	private int opened = 0;
	private int rating = 0;
	private MediaType type = MediaType.NONE;

	@javax.persistence.Transient // Only used for JSON serialisation
	protected String classinfo;

	/***************************************************************************
	 *                                                                         *
	 * Private transient fields                                                *
	 *                                                                         *
	 **************************************************************************/

	transient private String originId = "vidada.local";
	transient private MediaSource source;
	transient private IObservableCollection<Tag> _tags;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * ORM Constructor
	 */
	protected MediaItem() { }

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 * 
	 * @param parentLibrary
	 * @param relativeFilePath
	 */
	protected MediaItem(MediaLibrary parentLibrary, URI relativeFilePath) {
		this(new MediaSourceLocal(parentLibrary, relativeFilePath));
	}

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 * 
	 * @param mediaSource
	 */
	protected MediaItem(MediaSourceLocal mediaSource) {
		addSource(mediaSource);
		setFilename(NameUtil.prettifyName(mediaSource.getName()));
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Prototype this instance
	 * 
	 * @param prototype
	 */
	public void prototype(MediaItem prototype) {

		assert prototype != null;

		setFilename(prototype.getFilename());
		setFilehash(prototype.getFilehash());
		setType(prototype.getType());

		setSources(prototype.getSources());
		getTags().clear(); getTags().addAll(prototype.getTags());
		setRating(prototype.getRating());
		setOpened(prototype.getOpened());
		setAddedDate(prototype.getAddedDate());

		//super.prototype(prototype);
	}


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Gets the datetime when this media was added to the library
	 * 
	 * @return
	 */
	public DateTime getAddedDate() {
		return addedDate;
	}

	/**
	 * Sets the datetime when this media was added to the library
	 * 
	 * @param addedDate
	 */
	public void setAddedDate(DateTime addedDate) {
		this.addedDate = addedDate;
		firePropertyChange("addedDate");
	}

    /**
     * Gets the file size in bytes
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the file size in bytes
     * @param fileSize
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        firePropertyChange("fileSize");
    }

	/**
	 * Is the primary media source available
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		MediaSource source = getSource();
		return source != null && source.isAvailable();
	}

	/**
	 * Gets the best source for this media item. 
	 * It will return one that is currently available - if possible.
	 * @return
	 */
	@Transient
	public MediaSource getSource(){		
		if(source == null || !source.isAvailable())
		{
			Set<MediaSource> sources = getSources();
			if(sources != null)
				for (MediaSource s : sources) {
					if(s.isAvailable())
						source = s;
				}
			if(source != null && !sources.isEmpty())
				source = Lists.getFirst(sources);
		}
		return source;
	}

	/**
	 * Gets all known sources of this media item
	 * 
	 * @return
	 */
	public Set<MediaSource> getSources() {
		if(this.sources != null)
			return new HashSet<MediaSource>(this.sources);
		else {
			return new HashSet<MediaSource>();
		}
	}

	/**
	 * Internal access to set the sources
	 * 
	 * @param sources
	 */
	protected void setSources(Set<MediaSource> sources) {
		this.sources = sources;
		firePropertyChange("sources");
	}

	/**
	 * Add the given source to this media item
	 * 
	 * @param source
	 */
	public void addSource(MediaSource source) {
		if (!getSources().contains(source)) {
			this.sources.add(source);
			firePropertyChange("sources");
		}
	}

	/**
	 * Remove the given source from this media item
	 * 
	 * @param source
	 */
	public void removeSource(MediaSource source) {
		if (getSources().contains(source)) {
			this.sources.remove(source);
			firePropertyChange("sources");
		}
	}

	/**
	 * Gets all tags currently assigned to this media item
	 * 
	 * @return
	 */
	public IObservableCollection<Tag> getTags() {
		if(_tags == null){
			_tags = new ObservableCollection<Tag>(tags);
		}
		return _tags;
	}

	/**
	 * Checks if this MediaData has the given Tag assigned
	 * 
	 * @param tag
	 * @return
	 */
	@Transient
	public boolean hasTag(Tag tag) {
		return getTags().contains(tag);
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType mediaType) {
		if(mediaType.equals(MediaType.NONE)) throw new IllegalArgumentException("mediaType must not be NONE!");
		this.type = mediaType;
		firePropertyChange("mediaType");
	}

	/**
	 * Gets how many times this media item was opened already
	 * 
	 * @return
	 */
	public int getOpened() {
		return opened;
	}

	/**
	 * Set how many times this media item was opened
	 * 
	 * @param opened
	 */
	public void setOpened(int opened) {
		this.opened = opened;
		firePropertyChange("opened");
	}


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
		firePropertyChange("filename");
	}


	/**
	 * Returns a Hash from the file contents
	 * If the hash is not yet in the db, it will be created
	 * (This may take some time)
	 * @return
	 */
	public String getFilehash() {
		if (!isHashCalculated()) {
			MediaSource source = getSource();
			if(source != null && source.isAvailable()){
				MediaHashUtil hashUtil = MediaHashUtil.getDefaultMediaHashUtil();
				String hash =  hashUtil.retrieveFileHash(source.getResourceLocation());
				setFilehash(hash);
			}
		}
		return filehash;
	}

	/**
	 * Set the filehash of this media data
	 * @param filehash
	 */
	protected void setFilehash(String filehash) {
		this.filehash = filehash;
	}

	/**
	 * Returns the base id for the thumb images of this media
	 * 
	 * @return
	 */
	@Transient
	public String getThumbId() {
		return getFilehash();
	}

	@Transient
	public boolean isHashCalculated() {
		return filehash != null;
	}

	/**
	 * Get the image/video resolution of this media
	 * @return
	 */
	public Size getResolution() {
		return resolution;
	}

	/**
	 * Set the image/video resolution
	 * @param resolution
	 */
	public void setResolution(Size resolution) {
		this.resolution = resolution;
		firePropertyChange("resolution");
	}

	/**
	 * Has this media a resolution information?
	 * @return
	 */
	@Transient
	public boolean hasResolution(){ return resolution != null && !resolution.isEmpty();}


	/**
	 * Get the rating of this media
	 * @return
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * Set the rating of this media
	 * @param rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
		firePropertyChange("rating");
	}

	/**
	 * Gets the origin id of this media. (Usually a media store)
	 * Note that this value is transient!
	 * 
	 * @return
	 */
	public String getOriginId() {
		return originId;
	}

	/**
	 * Sets the origin id of this media
	 * Note that this value is transient!
	 * 
	 * @param originId
	 */
	public void setOriginId(String originId) {
		this.originId = originId;
	}


	@Override
	public String toString() {
		return this.getFilename() + "hash: " + this.getFilehash() + " src: " + getSource();
	}

	@Override
	public abstract MediaItem clone();

	/***************************************************************************
	 *                                                                         *
	 * HACK / TO BE REMOVED                                                    *
	 *                                                                         *
	 **************************************************************************/


	// HACK: Used by the android grid-cell view to handle selection
	// TODO: Refactor this away since this belongs not in the model layer
	transient private boolean selected = false;
	public void setSelection(boolean state) {
		selected = state;
	}
	public boolean isSelected() {
		return selected;
	}

}
