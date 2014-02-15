package vidada.repositories.db4o;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.repositories.IMediaRepository;
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
public class MediaRepositoryDb4o implements IMediaRepository {

	QueryBuilderDB4O queryBuilderDB4O = new QueryBuilderDB4O();

	public MediaRepositoryDb4o(){

	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#query(vidada.model.media.MediaQuery)
	 */
	@Override
	public List<MediaItem> query(MediaQuery qry){
		Query db4oQry = queryBuilderDB4O.buildMediadataCriteria(qry);
		return db4oQry.execute();
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#query(java.util.Collection)
	 */
	@Override
	public List<MediaItem> query(Collection<MediaLibrary> libraries){

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

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#query(vidada.model.tags.Tag)
	 */
	@Override
	public Collection<MediaItem> query(Tag tag){
		Query qry = queryBuilderDB4O.buildMediadataCriteria(tag);
		return qry.execute();
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#store(vidada.model.media.MediaItem)
	 */
	@Override
	public void store(MediaItem mediadata) {
		store(Lists.asList(mediadata));
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#store(java.lang.Iterable)
	 */
	@Override
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


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#delete(vidada.model.media.MediaItem)
	 */
	@Override
	public void delete(MediaItem mediadata) {
		delete(Lists.asList(mediadata));
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#delete(java.lang.Iterable)
	 */
	@Override
	public void delete(Iterable<MediaItem> mediadatas){
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		{
			for (MediaItem md : mediadatas) {
				db.delete(md);
			}
		}
		db.commit();
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#getAllMediaData()
	 */
	@Override
	public List<MediaItem> getAllMediaData() {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		Query query = db.query();
		query.constrain(MediaItem.class);

		List<MediaItem> medias = query.execute();
		return Lists.newList(medias); 
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#update(vidada.model.media.MediaItem)
	 */
	@Override
	public void update(MediaItem mediadata) {
		update(Lists.asList(mediadata));
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#update(java.lang.Iterable)
	 */
	@Override
	public void update(Iterable<MediaItem> mediadatas) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		for (MediaItem mediaData : mediadatas) {
			db.store(mediaData);
		}
		db.commit();
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#removeAll()
	 */
	@Override
	public void removeAll() {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();

		for (MediaItem md : getAllMediaData()) {
			db.delete(md);
		}
		db.commit();
	}



	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaRepository#findMediaDataByHash(java.lang.String)
	 */
	@Override
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
