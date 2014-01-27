package vidada.model.tags;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaRepository;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Service which manages all Tags
 * @author IsNull
 *
 */
public class TagRepository  {

	private MediaRepository mediaRepository = new MediaRepository();

	public void addTag(Tag newTag){
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		db.store( newTag );
		db.commit();
	}

	public void addTags(Set<Tag> newTags){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		for (Tag tag : newTags) {
			db.store( tag );
		}
		db.commit();
	}

	public void removeTag(Tag tag){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		{
			Collection<MediaItem> medias = mediaRepository.findMediaItems(tag);

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


	public synchronized List<Tag> getAllTags(){

		List<Tag> tags = null;
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		Query query = db.query();
		query.constrain(Tag.class);
		tags = query.execute();
		tags = Lists.toList(tags);

		Collections.sort(tags);

		return tags;
	}


	public synchronized void update(Tag tag) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store(tag);
		db.commit();
	}

}
