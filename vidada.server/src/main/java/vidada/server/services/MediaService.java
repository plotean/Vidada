package vidada.server.services;

import java.util.Collection;
import java.util.List;

import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaItemFactory;
import vidada.model.media.MediaLibrary;
import vidada.model.media.MediaQuery;
import vidada.server.repositories.IMediaRepository;
import vidada.server.repositories.RepositoryProvider;
import vidada.services.IMediaLibraryService;
import vidada.services.IMediaService;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.ResourceLocation;

public class MediaService implements IMediaService {

	transient private final IMediaRepository repository = RepositoryProvider.Resolve(IMediaRepository.class);

	private final IMediaLibraryService mediaLibraryService;

	public MediaService(IMediaLibraryService mediaLibraryService){
		this.mediaLibraryService = mediaLibraryService;
	}

	@Override
	public void store(MediaItem media) {
		repository.store(media);
	}

	@Override
	public void store(Collection<MediaItem> media) {
		repository.store(media);
	}

	@Override
	public void update(MediaItem media) {
		repository.update(media);
	}

	@Override
	public void update(Collection<MediaItem> media) {
		repository.update(media);
	}

	@Override
	public List<MediaItem> query(MediaQuery qry) {
		return repository.query(qry);
	}

	@Override
	public List<MediaItem> getAllMedias(){
		return repository.getAllMedias();
	}

	@Override
	public void delete(MediaItem media) {
		repository.delete(media);
	}

	@Override
	public void delete(Collection<MediaItem> media) {
		repository.delete(media);
	}

	/**
	 * Finds an existing media item or creates a new one for the given file
	 * @param file
	 * @param persist
	 * @return
	 */
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
	private MediaItem findAndCreateMedia(ResourceLocation resource, boolean canCreate, boolean persist){
		MediaItem mediaData;

		// We assume the given file is an absolute file path so we search for
		// a matching media library to substitute the library path

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

	/**
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
	private String retriveMediaHash(ResourceLocation file){
		return MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(file);
	}
}
