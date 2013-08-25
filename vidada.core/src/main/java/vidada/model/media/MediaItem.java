package vidada.model.media;

import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.entities.BaseEntity;
import vidada.model.images.IImageService;
import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.Tag;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.swing.components.thumbpresenter.items.IMediaDataThumb;
import archimedesJ.util.Lists;

import com.db4o.config.annotations.Indexed;

/**
 * Represents a single media item which can be indexed
 * 
 * @author IsNull
 * 
 */
public abstract class MediaItem extends BaseEntity implements IMediaDataThumb, IHaveMediaData {

	transient protected final IImageService imageService = ServiceProvider.Resolve(IImageService.class);


	private Set<MediaSource> sources = new HashSet<MediaSource>();
	private Set<Tag> tags = new HashSet<Tag>();

	@Indexed
	private String filename = null;
	@Indexed
	private String filehash = null;
	@Indexed
	private DateTime addedDate = new DateTime();
	@Indexed
	private Size resolution = null;
	@Indexed
	private int opened = 0;
	@Indexed
	private int rating = 0;
	@Indexed
	private MediaType mediaType;


	transient private MediaSource source;
	transient private final EventHandlerEx<EventArgsG<Tag>> tagAddedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	transient private final EventHandlerEx<EventArgsG<Tag>> tagRemovedEvent = new EventHandlerEx<EventArgsG<Tag>>();





	/**
	 * Raised when a Tag has been added to this media
	 * @return
	 */
	@Transient
	public IEvent<EventArgsG<Tag>> getTagAddedEvent() {
		return tagAddedEvent;
	}

	/**
	 *  Raised when a Tag has been removed form this media
	 * @return
	 */
	@Transient
	public IEvent<EventArgsG<Tag>> getTagRemovedEvent() {
		return tagRemovedEvent;
	}

	protected MediaItem() { }

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 * 
	 * @param parentLibrary
	 * @param relativeFilePath
	 */
	protected MediaItem(MediaLibrary parentLibrary, URI relativeFilePath) {
		this(new FileMediaSource(parentLibrary, relativeFilePath));
	}

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 * 
	 * @param parentLibrary
	 * @param relativeFilePath
	 */
	protected MediaItem(MediaSource mediaSource) {
		addSource(mediaSource);
		setFilename(prettifyName(mediaSource.getName()));
	}

	/**
	 * Prototype this instance
	 * 
	 * @param prototype
	 */
	protected void prototype(MediaItem prototype) {

		assert prototype != null;

		setFilename(prototype.getFilename());
		setFilehash(prototype.getFilehash());
		setMediaType(prototype.getMediaType());

		setSources(prototype.getSources());
		setTags(prototype.getTags());
		setRating(prototype.getRating());
		setOpened(getOpened());
		setAddedDate(prototype.getAddedDate());

		super.prototype(prototype);
	}

	/**
	 * Makes the given string better readable by removing special chars
	 * 
	 * @param rawName
	 * @return
	 */
	private static String prettifyName(String rawName) {

		rawName = rawName.replaceAll("\\[.*?\\]", "");
		rawName = rawName.replaceAll("\\(.*?\\)", "");

		rawName = rawName.replaceAll("[\\.|_|-]", " ");

		return rawName.trim();
	}

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
	 * Is the primary media source available
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		MediaSource source = getSource();
		return source != null && source.isAvailable();
	}

	/**
	 * Is this media a member of the given library?
	 * 
	 * @param library
	 * @return
	 */
	public boolean isMemberofLibrary(MediaLibrary library) {
		for (MediaSource s : this.getSources()) {
			return(s instanceof FileMediaSource &&
					((FileMediaSource) s).getParentLibrary().equals(library));
		}
		return false;
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
			for (MediaSource s : sources) {
				if(s.isAvailable())
					source = s;
			}
			if(source == null && !sources.isEmpty())
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
		return new HashSet<MediaSource>(this.sources);
	}

	/**
	 * Internal access to set the sources
	 * 
	 * @param tags
	 */
	protected void setSources(Set<MediaSource> sources) {
		this.sources = sources;
		firePropertyChange("sources");
	}

	/**
	 * Add the given source to this media item
	 * 
	 * @param tag
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
	 * @param tag
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
	public Set<Tag> getTags() {
		return new HashSet<Tag>(this.tags);
	}

	/**
	 * internal access to set the tags
	 * 
	 * @param tags
	 */
	protected void setTags(Set<Tag> tags) {
		this.tags = tags;
		firePropertyChange("tags");
	}

	@Transient
	public String getTagsString() {
		String string = "";
		for (Tag tag : getTags()) {
			string += "{" + tag.getName() + "} ";
		}
		return string;
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

	/**
	 * Add the given tag to this media item
	 * 
	 * @param tag
	 */
	public void addTag(Tag tag) {
		if (!getTags().contains(tag)) {
			this.tags.add(tag);
			tagAddedEvent.fireEvent(this, EventArgsG.build(tag));
			firePropertyChange("tags");
		}
	}

	/**
	 * Remove the given tag from this media item
	 * 
	 * @param tag
	 */
	public void removeTag(Tag tag) {
		if (getTags().contains(tag)) {
			System.out.println("MediaData: -Removing Tag: '" + tag + "' from " + getFilename());
			this.tags.remove(tag);
			tagRemovedEvent.fireEvent(this, EventArgsG.build(tag));
			firePropertyChange("tags");
		}
	}

	/**
	 * Add all given tags from this media item
	 * 
	 * @param newtags
	 */
	public void addTags(Set<Tag> newtags) {
		for (Tag tag : newtags) {
			addTag(tag);
		}
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
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
	protected void setOpened(int opened) {
		this.opened = opened;
		firePropertyChange("opened");
	}

	/**
	 * Open this media By default, the media is opened with the standard tool
	 * registered in the current Operating System
	 */
	public boolean open() {
		boolean success = false;

		MediaSource source = getSource();
		if(source != null && source.isAvailable()){
			success = source.open();
			if(success){
				setOpened(getOpened() + 1);
				persist();
			}
		}

		return success;
	}


	@Override
	public synchronized ImageContainer getThumbnail(Size size) {
		ImageContainer container = imageService.retrieveImageContainer(
				this,
				size,
				imageChangedCallBack);

		return container;
	} 

	private final ImageChangedCallback imageChangedCallBack = new ImageChangedCallback(){
		@Override
		public void imageChanged(ImageContainer container) {
			System.out.println("imageChangedCallBack: --> firePropertyChange('thumbnail'): raw-image: " + container.getRawImage());
			firePropertyChange("thumbnail");
		}
	};




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
			if(source != null)
				setFilehash(source.retriveHash());
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
	 * Get the display title of this media
	 */
	@Transient
	@Override
	public String getTitle() {
		return getFilename();
	}

	/**
	 * Get the description of this media
	 */
	@Transient
	@Override
	public String getDescription() {
		return "<no description>";
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
	public boolean hasResolution(){ return resolution != null;}

	/**
	 * Try to set the resolution
	 */
	@Transient
	public abstract void resolveResolution();

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
	 * Persist changes made to this media item
	 */
	public synchronized void persist() {
		IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
		mediaService.update(this);
	}


	/**
	 * Informs that a property has been changed
	 */
	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		super.firePropertyChange(propertyName, oldValue, newValue);
	}


	@Override
	public String toString() {
		return this.getFilename() + "hash: " + this.getFilehash() + " src: " + getSource();
	}

	@Transient
	@Override
	public MediaItem getMediaData() {
		return this;
	}

	//
	// IBaseThumb - a bit hacky to handle the view stuff here but yeah,
	// we can avoid a mapping between the MediaData model and the thumb media
	// item this way.
	//
	//

	private transient boolean isSelected;
	private transient boolean isHovered;

	@Transient
	@Override
	public boolean isSelected() {
		return isSelected;
	}

	@Transient
	@Override
	public void setSelection(boolean selection) {
		isSelected = selection;
	}

	@Transient
	@Override
	public boolean isHovered() {
		return isHovered;
	}

	@Transient
	@Override
	public void onMouseEnter(MouseEvent e) {
		isHovered = true;
	}

	@Transient
	@Override
	public void onMouseLeave(MouseEvent e) {
		isHovered = false;
	}

	@Transient
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Transient
	@Override
	public Integer getIdentifier(){
		return getFilehash().hashCode();
	}



}
