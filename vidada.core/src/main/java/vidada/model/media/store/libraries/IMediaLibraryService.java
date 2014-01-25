package vidada.model.media.store.libraries;

import java.util.List;

import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.ResourceLocation;

public interface IMediaLibraryService {

	/**
	 * Raised when a new Library has been added to this service
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaLibrary>> getLibraryAddedEvent();

	/**
	 * Raised when a new Library has removed from this service
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaLibrary>> getLibraryRemovedEvent();

	/**
	 * 
	 * @param id
	 * @return
	 */
	public abstract MediaLibrary getById(long id);

	/**
	 * returns all libraries
	 * @return
	 */
	public abstract List<MediaLibrary> getAllLibraries();

	/**
	 * Adds the given library and persists it
	 * @param lib
	 */
	public abstract void addLibrary(MediaLibrary lib);


	/**
	 * Removes the given library
	 * @param lib
	 */
	public abstract void removeLibrary(MediaLibrary lib);

	/**
	 * Updates the given entity
	 * @param lib
	 */
	public abstract void update(MediaLibrary lib);

	/**
	 * Find the MediaLibrary which may hold this file
	 * @param file
	 * @return
	 */
	public abstract MediaLibrary findLibrary(ResourceLocation file);



}