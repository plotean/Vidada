package vidada.model.media;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import archimedesJ.util.Debug;
import archimedesJ.util.Lists;
import archimedesJ.util.Objects;

import com.db4o.ObjectContainer;
import com.db4o.query.Constraint;
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

	/**
	 * Query for all medias which match the given media-query
	 * @param qry
	 * @return
	 */
	public Collection<MediaItem> query(MediaQuery qry){
		Query db4oQry = queryBuilderDB4O.buildMediadataCriteria(qry);
		return db4oQry.execute();
	}

	/**
	 * Returns all media items which are in the given libraries
	 * @param libraries
	 * @return
	 */
	public Collection<MediaItem> query(Collection<MediaLibrary> libraries){

		List<MediaItem> medias = null;
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		Query query = db.query();

		if(!libraries.isEmpty()){
			query.constrain(MediaItem.class);
			Constraint mediaLibConstraint = null;
			for (MediaLibrary mediaLib : libraries) {
				Constraint c = query.descend("sources").descend("parentLibrary").constrain(mediaLib);
				if(mediaLibConstraint == null)
					mediaLibConstraint = c;
				else
					mediaLibConstraint.or(c);
			}
			medias = query.execute();
		}else {
			medias = new ArrayList<MediaItem>();
		}

		return medias;
	}

	/**
	 * Query for all medias which have the given Tag
	 */
	public Collection<MediaItem> query(Tag tag){
		Query qry = queryBuilderDB4O.buildMediadataCriteria(tag);
		return qry.execute();
	}


	public void store(MediaItem mediadata) {
		store(Lists.asList(mediadata));
	}

	public void store(Iterable<MediaItem> mediadatas){

		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
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
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		{
			for (MediaItem md : mediadatas) {
				db.delete(md);
			}
		}
		db.commit();
	}


	public List<MediaItem> getAllMediaData() {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaItem.class);

		List<MediaItem> medias = query.execute();
		return Lists.newList(medias); 
	}

	public void update(MediaItem mediadata) {
		update(Lists.asList(mediadata));
	}

	public void update(Iterable<MediaItem> mediadatas) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		for (MediaItem mediaData : mediadatas) {
			db.store(mediaData);
		}
		db.commit();
	}


	public void removeAll() {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();

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

		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();


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
