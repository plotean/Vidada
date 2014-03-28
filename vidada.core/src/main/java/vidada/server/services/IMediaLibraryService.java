package vidada.server.services;

import archimedes.core.events.EventArgsG;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaLibrary;

import java.util.List;

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
	 * returns all libraries which are currently available
	 * @return
	 */
	public abstract List<MediaLibrary> getAvailableLibraries();

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