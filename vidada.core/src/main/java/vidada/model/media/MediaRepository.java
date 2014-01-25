package vidada.model.media;

import java.util.Collection;
import java.util.List;

import vidada.data.SessionManager;
import archimedesJ.util.Debug;
import archimedesJ.util.Lists;
import archimedesJ.util.Objects;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

/**
 * Media Repository handles the storage of media data
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class MediaRepository {

	QueryBuilderDB4O queryBuilderDB4O = new QueryBuilderDB4O();

	public MediaRepository(){

	}

	public Collection<MediaItem> query(MediaQuery qry){
		Query db4oQry = queryBuilderDB4O.buildMediadataCriteria(qry);
		return db4oQry.execute();
	}

	public void store(MediaItem mediadata) {
		store(Lists.asList(mediadata));
	}

	public void store(Iterable<MediaItem> mediadatas){

		ObjectContainer db =  SessionManager.getObjectContainer();
		try{

			for (MediaItem md : mediadatas) {
				db.store(md);
			}
			db.commit();
		}catch(Exception e){
			Debug.printAllLines("MediaService: Failed to add following medias:", mediadatas);
			e.printStackTrace();
		}
	}


	public void delete(MediaItem mediadata) {
		delete(Lists.asList(mediadata));
	}


	public void delete(Iterable<MediaItem> mediadatas){

		ObjectContainer db =  SessionManager.getObjectContainer();
		{
			for (MediaItem md : mediadatas) {
				db.delete(md);
			}
		}
		db.commit();
	}


	public List<MediaItem> getAllMediaData() {
		ObjectContainer db =  SessionManager.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaItem.class);

		List<MediaItem> medias = query.execute();
		return Lists.newList(medias); 
	}

	public void update(MediaItem mediadata) {
		update(Lists.asList(mediadata));
	}

	public void update(Iterable<MediaItem> mediadatas) {
		ObjectContainer db =  SessionManager.getObjectContainer();
		for (MediaItem mediaData : mediadatas) {
			db.store(mediaData);
		}
		db.commit();
	}


	public void removeAll() {
		ObjectContainer db =  SessionManager.getObjectContainer();

		for (MediaItem md : getAllMediaData()) {
			db.delete(md);
		}
		db.commit();
	}

	/**
	 * 
	 * @param hash
	 * @return
	 */
	public MediaItem findMediaDataByHash(final String hash){

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


}
