package vidada.model.media;

import java.net.URI;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

import vidada.data.SessionManager;
import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import archimedesJ.data.hashing.FileHashAlgorythms;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
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
public class MediaService implements IMediaService {

	private final IMediaLibraryService libraryService = ServiceProvider.Resolve(IMediaLibraryService.class);
	private final IFileHashAlgorythm fileHashAlgorythm = FileHashAlgorythms.instance().getButtikscheHashAlgorythm();
	private final MediaFileInfo[] supportedMedias = MediaFileInfo.getKnownMediaInfos();

	private transient IOFileFilter allMediaFileFilter;


	private EventHandlerEx<EventArgsG<MediaItem>> mediaDataAddedEvent = new EventHandlerEx<EventArgsG<MediaItem>>();
	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#getMediaDataAddedEvent()
	 */
	@Override
	public IEvent<EventArgsG<MediaItem>> getMediaDataAddedEvent() { return mediaDataAddedEvent; }

	private EventHandlerEx<EventArgsG<MediaItem>> mediaDataRemovedEvent = new EventHandlerEx<EventArgsG<MediaItem>>();
	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#getMediaDataRemovedEvent()
	 */
	@Override
	public IEvent<EventArgsG<MediaItem>> getMediaDataRemovedEvent() { return mediaDataRemovedEvent; }


	private EventHandlerEx<EventArgs> mediaDatasChangedEvent = new EventHandlerEx<EventArgs>();
	@Override
	public IEvent<EventArgs> getMediaDatasChangedEvent() { return mediaDatasChangedEvent; }


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
	public void addMediaData(List<MediaItem> mediadata){

		if(mediadata.isEmpty()) return;

		ObjectContainer db =  SessionManager.getObjectContainer();
		try{

			for (MediaItem md : mediadata) {
				db.store(md);
				onMediaDataAdded(md);
			}
			db.commit();
		}catch(Exception e){
			Debug.printAllLines("Failed to add following medias:", mediadata);
			e.printStackTrace();
		}
		mediaDatasChangedEvent.fireEvent(this, EventArgs.Empty);
	}


	@Override
	public void removeMediaData(MediaItem mediadata) {
		removeMediaData(Lists.asList(mediadata));
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#removeMediaData(java.util.List)
	 */
	@Override
	public void removeMediaData(List<MediaItem> mediadata){
		ObjectContainer db =  SessionManager.getObjectContainer();

		for (MediaItem md : mediadata) {
			db.delete(md);
			onMediaDataRemoved(md);
		}
		db.commit();
		mediaDatasChangedEvent.fireEvent(this, EventArgs.Empty);
	}


	protected void onMediaDataAdded(MediaItem mediadata){
		mediaDataAddedEvent.fireEvent(this, EventArgsG.build(mediadata));
	}

	protected void onMediaDataRemoved(MediaItem mediadata){
		mediaDataRemovedEvent.fireEvent(this, EventArgsG.build(mediadata));
	}



	@Override
	public List<MediaItem> getAllMediaData() {
		ObjectContainer db =  SessionManager.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaItem.class);
		List<MediaItem> medias = query.execute();

		return Lists.newList(medias); 
	}

	/**
	 * Search for the given media data by the given absolute path
	 */
	@Override
	public MediaItem findMediaData(ResourceLocation file) {
		MediaItem mediaData = findMediaDataByFilePath(file);
		if(mediaData == null)
		{
			mediaData = findMediaDataByHash(file);
		}
		return mediaData;
	}

	private MediaItem findMediaDataByHash(ResourceLocation file){
		final String hash = MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(file);

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
		else{
			System.err.println("findMediaData: Could not find file hash: " +  hash);
		}
		return mediaData;
	}


	private MediaItem findMediaDataByFilePath(ResourceLocation file){
		MediaItem mediaData = null;

		ObjectContainer db =  SessionManager.getObjectContainer();

		// we assume the given file is an absolute file path
		// so we search for a matching media library to
		// substitute the library path

		MediaLibrary library = libraryService.findLibrary(file);

		if(library != null)
		{
			final URI relativePath = library.getRelativePath(file);


			List<MediaItem> medias = db.query(new Predicate<MediaItem>() {
				@Override
				public boolean match(MediaItem media) {

					for (MediaSource s : media.getSources()) {
						if(s instanceof FileMediaSource)
							if(((FileMediaSource)s).getRelativeFilePath().equals(relativePath.getPath())){
								return true;
							}
					}
					return false;
				}
			});

			if(!medias.isEmpty())
				mediaData = medias.get(0);
			else{
				System.err.println("findMediaData: Could not find file in lib. " +  file);
			}

		}else{
			System.err.println("file is outside any known library: " + file);
		}

		return mediaData;
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
		mediaDatasChangedEvent.fireEvent(this, EventArgs.Empty);
	}














}
