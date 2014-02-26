package vidada.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import vidada.model.media.MediaLibrary;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.IMediaLibraryRepository;
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
		return runUnitOfWork(new Callable<List<MediaLibrary>>() {
			@Override
			public List<MediaLibrary> call() throws Exception {
				return repository.getAllLibraries();
			}
		});
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
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				MediaLibrary libToDelete = repository.queryById(lib.getId());
				repository.delete(libToDelete);
				libraryRemovedEvent.fireEvent(this, EventArgsG.build(lib));
			}
		});
	}

	@Override
	public void update(final MediaLibrary lib) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.update(lib);
			}
		});
	}

	@Override
	public MediaLibrary findLibrary(final ResourceLocation file) {
		return runUnitOfWork(new Callable<MediaLibrary>() {
			@Override
			public MediaLibrary call() throws Exception {
				return repository.queryByLocation(file);
			}
		});
	}

	@Override
	public MediaLibrary getById(final long id) {
		return runUnitOfWork(new Callable<MediaLibrary>() {
			@Override
			public MediaLibrary call() throws Exception {
				return repository.queryById(id);
			}
		});
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
