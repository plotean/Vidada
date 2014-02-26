package vidada.server.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaItemFactory;
import vidada.model.media.MediaLibrary;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.IMediaRepository;
import vidada.services.IMediaLibraryService;
import vidada.services.IMediaService;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.ResourceLocation;

public class MediaService extends VidadaServerService implements IMediaService {

	transient private final IMediaRepository repository = getRepository(IMediaRepository.class);


	public MediaService(VidadaServer server) {
		super(server);
	}


	@Override
	public void store(final MediaItem media) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.store(media);
			}
		});
	}

	@Override
	public void store(final Collection<MediaItem> medias) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.store(medias);
			}
		});
	}

	@Override
	public void update(final MediaItem media) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.update(media);
			}
		});
	}

	@Override
	public void update(final Collection<MediaItem> medias) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.update(medias);
			}
		});
	}

	@Override
	public ListPage<MediaItem> query(final MediaQuery qry, final int pageIndex, final int maxPageSize) {
		return runUnitOfWork(new Callable<ListPage<MediaItem>>() {
			@Override
			public ListPage<MediaItem> call() throws Exception {
				return repository.query(qry, pageIndex, maxPageSize);
			}
		});
	}

	@Override
	public List<MediaItem> getAllMedias(){
		return runUnitOfWork(new Callable<List<MediaItem>>() {
			@Override
			public List<MediaItem> call() throws Exception {
				return repository.getAllMedias();
			}
		});
	}

	@Override
	public void delete(final MediaItem media) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.delete(media);
			}
		});

	}

	@Override
	public void delete(final Collection<MediaItem> media) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.delete(media);
			}
		});
	}


	@Override
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		return findAndCreateMedia(file, true, persist);
	}


	/**
	 * Search for the given media data by the given absolute path
	 */
	public MediaItem findMediaData(ResourceLocation file) {
		return findAndCreateMedia(file, false, false);
	}


	/**
	 * 
	 * @param resource
	 * @param canCreate
	 * @param persist
	 * @return
	 */
	private MediaItem findAndCreateMedia(final ResourceLocation resource, final boolean canCreate, final boolean persist){
		// We assume the given file is an absolute file path so we search for
		// a matching media library to substitute the library path

		return runUnitOfWork(new Callable<MediaItem>() {
			@Override
			public MediaItem call() throws Exception {
				MediaItem mediaData;

				IMediaLibraryService mediaLibraryService = getServer().getLibraryService();

				final MediaLibrary library = mediaLibraryService.findLibrary(resource);
				if(library != null){

					String hash = null;

					// first we search for the media

					mediaData = repository.queryByPath(resource, library);
					if(mediaData == null)
					{
						hash = retriveMediaHash(resource);
						if(hash != null)
							mediaData = repository.queryByHash(hash);
					}

					if(canCreate && mediaData == null){

						// we could not find a matching media so we create a new one

						mediaData = MediaItemFactory.instance().buildMedia(resource, library, hash);
						if(persist && mediaData != null){
							repository.store(mediaData);
						}
					}
				}else
					throw new NotSupportedException("resource is not part of any media library");

				return mediaData;
			}
		});

	}

	/**
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
	private String retriveMediaHash(ResourceLocation file){
		return MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(file);
	}


	@Override
	public int count() {
		return runUnitOfWork(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return repository.countAll();
			}
		}) ;
	}


	@Override
	public MediaItem queryByHash(final String hash) {
		return runUnitOfWork(new Callable<MediaItem>() {
			@Override
			public MediaItem call() throws Exception {
				return repository.queryByHash(hash);
			}
		}) ;
	}
}
