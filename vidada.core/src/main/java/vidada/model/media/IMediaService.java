package vidada.model.media;

import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

import vidada.model.libraries.MediaLibrary;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.services.IService;

public interface IMediaService extends IService{


	// Events

	/**
	 * Raised when a new Media has been added to the Library
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaItem>> getMediaDataAddedEvent();

	/**
	 * Raised when a Media has been removed from the Library
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaItem>> getMediaDataRemovedEvent();

	/**
	 * Raised when Media Datas have changed
	 * @return
	 */
	public abstract IEvent<EventArgs> getMediaDatasChangedEvent();

	/**
	 * Raised when the given Media Data has been changed
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaItem>> getMediaDataChangedEvent();



	/**
	 * Add the given mediadata to this service and persist it.
	 * @param mediadata
	 */
	public abstract void addMediaData(MediaItem mediadata);

	/**
	 * Removes the given mediadata from this service
	 * @param mediadata
	 */
	public abstract void removeMediaData(MediaItem mediadata);



	/**
	 * Add the given mediadatas to this service and persist it.
	 * @param mediadata
	 */
	public abstract void addMediaData(List<MediaItem> mediadata);

	/**
	 * Removes the given mediadata from this service
	 * @param mediadata
	 */
	public abstract void removeMediaData(List<MediaItem> mediadata);

	/**
	 * Persists the given mediadata
	 * @param mediadata
	 */
	public abstract void update(MediaItem mediadata);


	/**
	 * Persists the given media datas
	 * @param mediadata
	 */
	public abstract void update(Iterable<MediaItem> mediadata);


	/**
	 * Returns all mediadata in the scope of this MediaService
	 * @return
	 */
	public abstract List<MediaItem> getAllMediaData();

	/**
	 * Searches for a MediaData which matches the given file
	 * @param file
	 * @return
	 */
	public abstract MediaItem findMediaData(ResourceLocation file);


	/**
	 * Build a new Media from the given mediaLocation. The created media will not be added to this media service. 
	 * 
	 * Warning: This method will NOT check if already a media exists for this media-file!
	 * 
	 * @param mediaLocation
	 * @param parentlibrary
	 * @param mediahash The hash of the media. If null, will be calulcated by this method.
	 * @return
	 */
	public abstract MediaItem buildMedia(ResourceLocation mediaLocation, MediaLibrary parentlibrary, String mediahash);


	/**
	 * Searches for a media in the media database which matches the given file.
	 * If no match was found, a new media item will be created and returned.
	 * 
	 * If persist is true, a new created media will be added to this media service.
	 * 
	 * @param file
	 * @return
	 */
	public abstract MediaItem findOrCreateMedia(ResourceLocation file, boolean persist);


	public abstract IOFileFilter getAllMediaFileFilter();

	public abstract IFileHashAlgorythm getFileHashAlgorythm();

	public abstract IMediaImportService getMediaImporter();

	public abstract void removeAll();

}