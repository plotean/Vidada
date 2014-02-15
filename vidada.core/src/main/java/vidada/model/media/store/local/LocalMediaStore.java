package vidada.model.media.store.local;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaFileInfo;
import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaType;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.libraries.IMediaLibraryManager;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.media.store.libraries.MediaLibraryManager;
import vidada.model.tags.ILocalTagService;
import vidada.model.tags.ITagService;
import vidada.model.tags.LocalTagService;
import vidada.model.tags.Tag;
import vidada.repositories.IMediaRepository;
import vidada.repositories.db4o.MediaRepositoryDb4o;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
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

	public static final String Name = "local.store"; 

	transient private final MediaThumbFetcher localThumbFetcher = new MediaThumbFetcher();
	transient private final LocalImageCacheManager localImageCacheManager = new LocalImageCacheManager();

	transient private final IMediaRepository mediaRepository = new MediaRepositoryDb4o();
	transient private final IMediaLibraryManager libraryService = new MediaLibraryManager();
	transient private final ILocalTagService localTagService;

	public LocalMediaStore(ITagService tagService){
		localTagService = new LocalTagService(tagService);
	}

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
	public String getNameId() {
		return Name;
	}

	@Override
	public void update(MediaItem media) {
		mediaRepository.update(media);
	}

	@Override
	public void update(Collection<MediaItem> media) {
		mediaRepository.update(media);
	}

	@Override
	public List<MediaItem> query(MediaQuery qry) {
		return mediaRepository.query(qry);
	}


	@Override
	public IMemoryImage getThumbImage(MediaItem media, Size size) {
		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		if(imagecache != null){
			try {
				thumb = localThumbFetcher.fetchThumb(media, size, imagecache);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("LocalMediaStore: can not get image cache for " + media);
		}

		return thumb;
	}

	@Override
	public IMemoryImage renewThumbImage(MovieMediaItem media, Size size, float pos) {

		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		try {
			media.setPreferredThumbPosition(MovieMediaItem.INVALID_POSITION);
			media.setCurrentThumbPosition(MovieMediaItem.INVALID_POSITION);

			imagecache.removeImage(media.getFilehash());
			thumb = localThumbFetcher.fetchThumb(media, size, imagecache);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return thumb;
	}

	@Override
	public Collection<Tag> getAllUsedTags() {
		return getTagManager().getUsedTags(getLibraryManager().getAvailableLibraries());
	}


	@Override
	public void synchronize() {
		// TODO Auto-generated method stub

	}


	//
	// ================   Special abilities of local media store   ==============
	//


	public void delete(MediaItem media) {
		mediaRepository.delete(media);
	}

	public void delete(Collection<MediaItem> media) {
		mediaRepository.delete(media);
	}

	public IMediaLibraryManager getLibraryManager(){
		return libraryService;
	}

	public ILocalTagService getTagManager(){
		return localTagService ;
	}


	/**
	 * Simple media factory to create a media item from an existing file.
	 * The media type is determined dynamically. 
	 * @param mediaLocation
	 * @param parentlibrary
	 * @param mediahash
	 * @return
	 */
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
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
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
	private MediaItem findAndCreateMedia(ResourceLocation resource, boolean canCreate, boolean persist){
		MediaItem mediaData;

		// We assume the given file is an absolute file path so we search for
		// a matching media library to substitute the library path

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

		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();

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
