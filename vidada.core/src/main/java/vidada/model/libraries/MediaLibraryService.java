package vidada.model.libraries;

import java.net.URI;
import java.util.List;

import vidada.data.SessionManager;
import vidada.model.media.MediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import db4o.Db4oUtil;

/**
 * Manages all MediaLibraries
 * @author IsNull
 *
 */
public class MediaLibraryService implements IMediaLibraryService {

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
		ObjectContainer db =  SessionManager.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaLibrary.class);
		List<MediaLibrary> libs = query.execute();
		return Lists.newList(libs);
	}



	/* (non-Javadoc)
	 * @see vidada.model.libraries.IMediaLibraryService#addLibrary(vidada.model.libraries.MediaLibrary)
	 */
	@Override
	public void addLibrary(MediaLibrary lib){
		ObjectContainer db =  SessionManager.getObjectContainer();
		db.store( lib );
		libraryAddedEvent.fireEvent(this, EventArgsG.build(lib));
	}



	@Override
	public void removeLibrary(final MediaLibrary lib) {
		try{
			System.out.println("removing MediaLibrary " + lib + " from Database...");

			ObjectContainer db =  SessionManager.getObjectContainer();



			List<MediaItem> toDelete = db.query(new Predicate<MediaItem>() {
				@Override
				public boolean match(MediaItem media) {
					for(MediaSource s : media.getSources())
					{
						if(s instanceof FileMediaSource)
							return (((FileMediaSource) s).getParentLibrary() == lib);
					}
					return false;
				}
			});

			// first remove all Medias in this library

			for (MediaItem mediaItem : toDelete) {
				for(MediaSource s : Lists.newList(mediaItem.getSources()))
				{
					if(s instanceof FileMediaSource)
						if (((FileMediaSource) s).getParentLibrary() == lib){
							mediaItem.removeSource(s);
							db.store(mediaItem);
							db.delete(s);
						}
				}

				if(mediaItem.getSources().size() == 0)
					db.delete(mediaItem);
			}

			// now we can delete the library
			db.delete(lib);
			libraryRemovedEvent.fireEvent(this, EventArgsG.build(lib));

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update(MediaLibrary lib) {
		ObjectContainer db =  SessionManager.getObjectContainer();
		db.store( lib );
	}

	@Override
	public MediaLibrary findLibrary(ResourceLocation file) {
		List<MediaLibrary> allLibs = getAllLibraries();

		for (MediaLibrary mediaLibrary : allLibs) {
			URI relPath = mediaLibrary.getRelativePath(file);

			if(relPath != null)
				return mediaLibrary;
		}

		return null;
	}

	@Override
	public MediaLibrary getById(long id) {
		return Db4oUtil.getById(SessionManager.getObjectContainer(), id);
	}


}
