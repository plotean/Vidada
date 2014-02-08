package vidada.model.tags;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaRepository;
import vidada.model.media.store.libraries.MediaLibrary;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Service which manages persistence of tags
 * @author IsNull
 *
 */
public class TagRepository  {

	private MediaRepository mediaRepository = new MediaRepository();

	public void store(Tag newTag){
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		db.store( newTag );
		db.commit();
	}

	public void store(Set<Tag> newTags){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		for (Tag tag : newTags) {
			db.store( tag );
		}
		db.commit();
	}

	public void delete(Tag tag){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		{
			Collection<MediaItem> medias = mediaRepository.query(tag);

			if(!medias.isEmpty())
			{
				// There are medias currently using the tag which will be deleted
				// Remove this tag from those medias first.
				for (MediaItem mediaItem : medias) {
					mediaItem.removeTag(tag);
					db.store(mediaItem);
				}
			}

			db.delete(tag);
			db.commit();
		}
	}

	/**
	 * Fetches all Tags which are used in the provided media libraries
	 * @return
	 */
	public Collection<Tag> getAllUsedTags(Collection<MediaLibrary> libraries){

		Set<Tag> tags = new HashSet<Tag>();

		Collection<MediaItem> medias = mediaRepository.query(libraries);

		// Might be very slow !!
		for (MediaItem media : medias) {
			tags.addAll(media.getTags());
		}

		return tags;
	}



	/**
	 * Fetches all tags
	 * @return
	 */
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


	public void update(Tag tag) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store(tag);
		db.commit();
	}

}
