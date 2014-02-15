package vidada.repositories.db4o;

import java.net.URI;
import java.util.List;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.MediaItem;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.repositories.IMediaLibraryRepository;
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
public class MediaLibraryRepositoryDb4o implements IMediaLibraryRepository {


	@Override
	public List<MediaLibrary> getAllLibraries(){
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		{
			Query query = db.query();
			query.constrain(MediaLibrary.class);
			List<MediaLibrary> libs = query.execute();

			for (MediaLibrary mediaLibrary : libs) {
				db.activate(mediaLibrary, 4);
			}

			return Lists.newList(libs);
		}
	}



	@Override
	public void store(MediaLibrary lib){
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store( lib );
		db.commit();
	}



	@Override
	public void delete(final MediaLibrary lib) {
		try{
			System.out.println("removing MediaLibrary " + lib + " from Database...");

			ObjectContainer db =  SessionManagerDB4O.getObjectContainer();


			// find all local medias which have a source 
			List<MediaItem> toDelete = db.query(new Predicate<MediaItem>() {
				@Override
				public boolean match(MediaItem media) {
					for(IMediaSource s : media.getSources())
					{
						if(((MediaSourceLocal)s).getParentLibrary() == lib)
							return true;
					}
					return false;
				}
			});

			// first remove all Medias in this library

			for (MediaItem mediaItem : toDelete) {
				for(IMediaSource s : Lists.newList(mediaItem.getSources()))
				{
					if (((MediaSourceLocal)s).getParentLibrary() == lib){
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

			db.commit();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update(MediaLibrary lib) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store( lib );
	}

	@Override
	public MediaLibrary queryByLocation(ResourceLocation file) {
		List<MediaLibrary> allLibs = getAllLibraries();

		for (MediaLibrary mediaLibrary : allLibs) {
			URI relPath = mediaLibrary.getMediaDirectory().getRelativePath(file);

			if(relPath != null)
				return mediaLibrary;
		}

		return null;
	}

	@Override
	public MediaLibrary queryById(long id) {
		return Db4oUtil.getById(SessionManagerDB4O.getObjectContainer(), id);
	}

}
