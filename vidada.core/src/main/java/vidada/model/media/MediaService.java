package vidada.model.media;

import java.net.URI;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

import vidada.data.SessionManager;
import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.media.source.MediaSource;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.data.hashing.FileHashAlgorythms;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.Debug;
import archimedesJ.util.FileSupport;
import archimedesJ.util.Lists;
import archimedesJ.util.Objects;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

/**
 * Implementation of the IMediaService
 * Manages all Media entities
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class MediaService implements IMediaService {

	private final IMediaLibraryService libraryService = ServiceProvider.Resolve(IMediaLibraryService.class);
	private final IFileHashAlgorythm fileHashAlgorythm = FileHashAlgorythms.instance().getButtikscheHashAlgorythm();
	private final MediaFileInfo[] supportedMedias = MediaFileInfo.getKnownMediaInfos();

	private transient IOFileFilter allMediaFileFilter;



	private EventHandlerEx<CollectionEventArg<MediaItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<MediaItem>>();
	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#getMediaDataAddedEvent()
	 */
	@Override
	public IEvent<CollectionEventArg<MediaItem>> getMediasChangedEvent() { return mediasChangedEvent; }



	private EventHandlerEx<EventArgsG<MediaItem>> mediaDataChangedEvent = new EventHandlerEx<EventArgsG<MediaItem>>();
	@Override
	public IEvent<EventArgsG<MediaItem>> getMediaDataChangedEvent() { return mediaDataChangedEvent; }


	public MediaService(){

		String[] allMediaExtensions = new String[0];
		for (MediaFileInfo media : supportedMedias) {
			allMediaExtensions = Lists.concat(allMediaExtensions, media.getFileExtensions());
		}
		allMediaFileFilter = FileSupport.extensionFilter(allMediaExtensions);

	}


	@Override
	public IOFileFilter getAllMediaFileFilter(){
		return allMediaFileFilter;
	}

	@Override
	public IFileHashAlgorythm getFileHashAlgorythm(){
		return this.fileHashAlgorythm;
	}


	@Override
	public void addMediaData(MediaItem mediadata) {
		addMediaData(Lists.asList(mediadata));
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#addMediaData(java.util.List)
	 */
	@Override
	public void addMediaData(List<MediaItem> mediadatas){

		if(mediadatas.isEmpty()) return;

		ObjectContainer db =  SessionManager.getObjectContainer();
		try{

			for (MediaItem md : mediadatas) {
				db.store(md);
				onMediaDataAdded(md);
			}
			db.commit();
		}catch(Exception e){
			Debug.printAllLines("MediaService: Failed to add following medias:", mediadatas);
			e.printStackTrace();
		}
		onMediaDataAdded(mediadatas.toArray(new MediaItem[0]));
	}


	@Override
	public void removeMediaData(MediaItem mediadata) {
		removeMediaData(Lists.asList(mediadata));
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#removeMediaData(java.util.List)
	 */
	@Override
	public void removeMediaData(List<MediaItem> mediadatas){
		ObjectContainer db =  SessionManager.getObjectContainer();

		for (MediaItem md : mediadatas) {
			db.delete(md);
			onMediaDataRemoved(md);
		}
		db.commit();
		onMediaDataRemoved(mediadatas.toArray(new MediaItem[0]));
	}


	protected void onMediaDataAdded(MediaItem... mediadata){
		mediasChangedEvent.fireEvent(this, new CollectionEventArg<MediaItem>(CollectionChangeType.Added, mediadata));
	}

	protected void onMediaDataRemoved(MediaItem... mediadata){
		mediasChangedEvent.fireEvent(this, new CollectionEventArg<MediaItem>(CollectionChangeType.Removed, mediadata));
	}



	@Override
	public List<MediaItem> getAllMediaData() {
		ObjectContainer db =  SessionManager.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaItem.class);

		List<MediaItem> medias = query.execute();
		return Lists.newList(medias); 
	}



	@Override
	public void update(MediaItem mediadata) {
		update(Lists.asList(mediadata));
	}

	@Override
	public void update(Iterable<MediaItem> mediadatas) {
		ObjectContainer db =  SessionManager.getObjectContainer();
		for (MediaItem mediaData : mediadatas) {
			db.store(mediaData);
		}
		db.commit();
	}

	@Override
	public IMediaImportService getMediaImporter() {
		return new MediaImportService(this);
	}



	@Override
	public void removeAll() {
		ObjectContainer db =  SessionManager.getObjectContainer();

		for (MediaItem md : getAllMediaData()) {
			db.delete(md);
		}
		db.commit();
		mediasChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
	}


	@Override
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


	@Override
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		return findAndCreateMediaHelper(file, true, persist);
	}


	/**
	 * Search for the given media data by the given absolute path
	 */
	@Override
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
					mediaData = findMediaDataByHash(hash);
			}

			if(canCreate && mediaData == null){

				// we could not find a matching media so we create a new one

				mediaData = buildMedia(resource, library, hash);
				if(persist && mediaData != null){
					addMediaData(mediaData);
				}
			}
		}else
			throw new NotSupportedException("resource is not part of any media library");

		return mediaData;
	}


	private MediaItem findMediaDataByHash(final String hash){

		MediaItem mediaData = null;

		ObjectContainer db =  SessionManager.getObjectContainer();


		List<MediaItem> medias = db.query(new Predicate<MediaItem>() {
			@Override
			public boolean match(MediaItem media) {
				return Objects.equals(media.getFilehash(), hash);
			}
		});

		if(!medias.isEmpty())
			mediaData = medias.get(0);
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
					for (MediaSource s : media.getSources()) {
						if(s.getRelativeFilePath().equals(relativePath.getPath())){
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
