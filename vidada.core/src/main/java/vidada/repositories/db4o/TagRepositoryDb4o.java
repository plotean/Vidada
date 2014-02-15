package vidada.repositories.db4o;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.MediaItem;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.repositories.IMediaRepository;
import vidada.repositories.ITagRepository;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Manages persistence of tags
 * @author IsNull
 *
 */
public class TagRepositoryDb4o implements ITagRepository  {

	private IMediaRepository mediaRepository = new MediaRepositoryDb4o();

	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#store(vidada.model.tags.Tag)
	 */
	@Override
	public void store(Tag newTag){
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		db.store( newTag );
		db.commit();
	}

	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#store(java.util.Set)
	 */
	@Override
	public void store(Set<Tag> newTags){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		for (Tag tag : newTags) {
			db.store( tag );
		}
		db.commit();
	}

	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#delete(vidada.model.tags.Tag)
	 */
	@Override
	public void delete(Tag tag){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		{
			Collection<MediaItem> medias = mediaRepository.query(tag);

			if(!medias.isEmpty())
			{
				// There are medias currently using the tag which will be deleted
				// Remove this tag from those medias first.
				for (MediaItem mediaItem : medias) {
					mediaItem.getTags().remove(tag);
					db.store(mediaItem);
				}
			}

			db.delete(tag);
			db.commit();
		}
	}

	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#getAllUsedTags(java.util.Collection)
	 */
	@Override
	public Collection<Tag> getAllUsedTags(Collection<MediaLibrary> libraries){

		Set<Tag> tags = new HashSet<Tag>();

		Collection<MediaItem> medias = mediaRepository.query(libraries);

		// Might be very slow !!
		for (MediaItem media : medias) {
			tags.addAll(media.getTags());
		}

		return tags;
	}



	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#getAllTags()
	 */
	@Override
	public List<Tag> getAllTags(){

		List<Tag> tags = null;
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		Query query = db.query();
		query.constrain(Tag.class);
		tags = query.execute();
		tags = Lists.toList(tags);

		Collections.sort(tags);

		return tags;
	}


	/* (non-Javadoc)
	 * @see vidada.repositories.db4o.ITagRepository#update(vidada.model.tags.Tag)
	 */
	@Override
	public void update(Tag tag) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store(tag);
		db.commit();
	}

}
