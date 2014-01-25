package vidada.model.media.store.local;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import vidada.data.SessionManager;
import vidada.model.media.MediaFileInfo;
import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaRepository;
import vidada.model.media.MediaType;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.libraries.IMediaLibraryService;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.media.store.libraries.MediaLibraryService;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.ResourceLocation;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

/**
 * Represents a local media store which manages all  medias available locally.
 * 
 * @author IsNull
 *
 */
public class LocalMediaStore implements IMediaStore {

	private final String name = "local-store"; 

	transient private final MediaRepository mediaRepository = new MediaRepository();
	transient private final IMediaLibraryService libraryService = new MediaLibraryService();


	public void store(MediaItem media) {
		mediaRepository.store(media);
	}

	public void store(Collection<MediaItem> media) {
		mediaRepository.store(media);
	}


	//
	// IMediaStore implementation
	//

	@Override
	public void update(MediaItem media) {
		mediaRepository.update(media);
	}

	@Override
	public void update(Collection<MediaItem> media) {
		mediaRepository.update(media);
	}

	@Override
	public void delete(MediaItem media) {
		mediaRepository.delete(media);
	}

	@Override
	public void delete(Collection<MediaItem> media) {
		mediaRepository.delete(media);
	}

	@Override
	public Set<MediaItem> query(MediaQuery qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameId() {
		return name;
	}

	@Override
	public void synchronize() {
		// TODO Auto-generated method stub

	}


	//
	// ================   Special abilities of local media store   ==============
	//


	public IMediaLibraryService getMediaLibraryManager(){
		return libraryService;
	}



	public MediaItem buildMedia(final ResourceLocation mediaLocation, final MediaLibrary parentlibrary, String mediahash) {

		MediaItem newMedia = null;

		if(mediahash == null){
			// if no hash has been provided we have to calculate it now
			mediahash =  MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(mediaLocation);
		}

		// find the correct Media type for the given media
		if(MediaFileInfo.get(MediaType.MOVIE).isFileofThisType(mediaLocation))
		{
			newMedia = new MovieMediaItem(
					parentlibrary,
					parentlibrary.getMediaDirectory().getRelativePath(mediaLocation),
					mediahash);

		}else if(MediaFileInfo.get(MediaType.IMAGE).isFileofThisType(mediaLocation)){

			newMedia = new ImageMediaItem(
					parentlibrary,
					parentlibrary.getMediaDirectory().getRelativePath(mediaLocation),
					mediahash);

		}else {
			System.err.println("MediaService: Can not handle " + mediaLocation.toString());
		}

		return newMedia;
	}


	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		return findAndCreateMediaHelper(file, true, persist);
	}


	/**
	 * Search for the given media data by the given absolute path
	 */
	public MediaItem findMediaData(ResourceLocation file) {
		return findAndCreateMediaHelper(file, false, false);
	}

	private String retriveMediaHash(ResourceLocation file){
		return MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(file);
	}



	/**
	 * 
	 * @param resource
	 * @param canCreate
	 * @param persist
	 * @return
	 */
	private MediaItem findAndCreateMediaHelper(ResourceLocation resource, boolean canCreate, boolean persist){
		MediaItem mediaData;

		// we assume the given file is an absolute file path
		// so we search for a matching media library to
		// substitute the library path

		final MediaLibrary library = libraryService.findLibrary(resource);

		if(library != null){
			String hash = null;

			// first we search for the media

			mediaData = findMediaInLibrary(resource, library);
			if(mediaData == null)
			{
				hash = retriveMediaHash(resource);
				if(hash != null)
					mediaData = mediaRepository.findMediaDataByHash(hash);
			}

			if(canCreate && mediaData == null){

				// we could not find a matching media so we create a new one

				mediaData = buildMedia(resource, library, hash);
				if(persist && mediaData != null){
					mediaRepository.store(mediaData);
				}
			}
		}else
			throw new NotSupportedException("resource is not part of any media library");

		return mediaData;
	}


	private MediaItem findMediaInLibrary(ResourceLocation file, MediaLibrary library){
		MediaItem mediaData = null;

		ObjectContainer db =  SessionManager.getObjectContainer();

		if(library != null)
		{
			final URI relativePath = library.getMediaDirectory().getRelativePath(file);

			List<MediaItem> medias = db.query(new Predicate<MediaItem>() {
				@Override
				public boolean match(MediaItem media) {
					for (IMediaSource s : media.getSources()) {

						if(((MediaSourceLocal)s).getRelativeFilePath().equals(relativePath.getPath())){
							return true;
						}
					}
					return false;
				}
			});

			if(!medias.isEmpty())
				mediaData = medias.get(0);
			else{
				//System.err.println("findMediaDataByFilePath: Could not find file in lib. " +  file);
			}

		}else{
			System.err.println("file is outside any known library: " + file);
		}

		return mediaData;
	}



}
