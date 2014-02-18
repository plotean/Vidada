package vidada.server.services;

import java.util.ArrayList;
import java.util.List;

import vidada.model.media.MediaLibrary;
import vidada.server.repositories.IMediaLibraryRepository;
import vidada.server.repositories.RepositoryProvider;
import vidada.services.IMediaLibraryService;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Manages all MediaLibraries
 * @author IsNull
 *
 */
public class MediaLibraryService implements IMediaLibraryService {

	private final IMediaLibraryRepository repository = RepositoryProvider.Resolve(IMediaLibraryRepository.class);

	private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryAddedEvent = new EventHandlerEx<EventArgsG<MediaLibrary>>();
	private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryRemovedEvent = new EventHandlerEx<EventArgsG<MediaLibrary>>();

	@Override
	public IEvent<EventArgsG<MediaLibrary>> getLibraryAddedEvent() {return libraryAddedEvent; }

	@Override
	public IEvent<EventArgsG<MediaLibrary>> getLibraryRemovedEvent() {return libraryRemovedEvent; }

	/* (non-Javadoc)
	 * @see vidada.model.libraries.IMediaLibraryService#getAllLibraries()
	 */
	@Override
	public List<MediaLibrary> getAllLibraries(){
		return repository.getAllLibraries();
	}

	/* (non-Javadoc)
	 * @see vidada.model.libraries.IMediaLibraryService#addLibrary(vidada.model.libraries.MediaLibrary)
	 */
	@Override
	public void addLibrary(MediaLibrary lib){
		repository.store(lib);
		libraryAddedEvent.fireEvent(this, EventArgsG.build(lib));
	}

	@Override
	public void removeLibrary(final MediaLibrary lib) {
		repository.delete(lib);
		libraryRemovedEvent.fireEvent(this, EventArgsG.build(lib));
	}

	@Override
	public void update(MediaLibrary lib) {
		repository.update(lib);
	}

	@Override
	public MediaLibrary findLibrary(ResourceLocation file) {
		return repository.queryByLocation(file);
	}

	@Override
	public MediaLibrary getById(long id) {
		return repository.queryById(id);
	}

	@Override
	public List<MediaLibrary> getAvailableLibraries() {
		List<MediaLibrary> available = new ArrayList<MediaLibrary>();
		for (MediaLibrary library : getAllLibraries()) {
			if(library.isAvailable()){
				available.add(library);
			}
		} 
		return available;
	}
}
