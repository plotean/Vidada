package vidada.server.services;

import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaLibrary;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.IMediaLibraryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all MediaLibraries
 * @author IsNull
 *
 */
public class MediaLibraryService extends VidadaServerService implements IMediaLibraryService {

	private final IMediaLibraryRepository repository = getRepository(IMediaLibraryRepository.class);

	private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryAddedEvent = new EventHandlerEx<EventArgsG<MediaLibrary>>();
	private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryRemovedEvent = new EventHandlerEx<EventArgsG<MediaLibrary>>();

	@Override
	public IEvent<EventArgsG<MediaLibrary>> getLibraryAddedEvent() {return libraryAddedEvent; }

	@Override
	public IEvent<EventArgsG<MediaLibrary>> getLibraryRemovedEvent() {return libraryRemovedEvent; }


	public MediaLibraryService(VidadaServer server) {
		super(server);
	}


	/* (non-Javadoc)
	 * @see vidada.model.libraries.IMediaLibraryService#getAllLibraries()
	 */
	@Override
	public List<MediaLibrary> getAllLibraries(){
		return runUnitOfWork(() -> repository.getAllLibraries());
	}

	/* (non-Javadoc)
	 * @see vidada.model.libraries.IMediaLibraryService#addLibrary(vidada.model.libraries.MediaLibrary)
	 */
	@Override
	public void addLibrary(final MediaLibrary lib){
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.store(lib);
				libraryAddedEvent.fireEvent(this, EventArgsG.build(lib));
			}
		});
	}

	@Override
	public void removeLibrary(final MediaLibrary lib) {
		runUnitOfWork(() -> {
				MediaLibrary libToDelete = repository.queryById(lib.getId());
				repository.delete(libToDelete);
				libraryRemovedEvent.fireEvent(this, EventArgsG.build(lib));
		});
	}

	@Override
	public void update(final MediaLibrary lib) {
		runUnitOfWork(() -> {
            repository.update(lib);
        });
	}

	@Override
	public MediaLibrary findLibrary(final ResourceLocation file) {
		return runUnitOfWork(() -> repository.queryByLocation(file));
	}

	@Override
	public MediaLibrary getById(final long id) {
		return runUnitOfWork(() -> repository.queryById(id));
	}

	@Override
	public List<MediaLibrary> getAvailableLibraries() {
		List<MediaLibrary> available = new ArrayList<>();
		for (MediaLibrary library : getAllLibraries()) {
			if(library.isAvailable()){
				available.add(library);
			}
		} 
		return available;
	}
}
