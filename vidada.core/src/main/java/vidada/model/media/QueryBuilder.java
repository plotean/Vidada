package vidada.model.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.SessionManager;
import vidada.model.ServiceProvider;
import vidada.model.libraries.MediaLibrary;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;

import com.db4o.ObjectContainer;
import com.db4o.query.Constraint;
import com.db4o.query.Query;

public class QueryBuilder {

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);

	/**
	 * Returns all tags that remain
	 *  
	 * @param medias
	 * @return
	 */
	public static Set<Tag> getRemainingTags(List<MediaItem> medias){

		Set<Tag> remainders = new HashSet<Tag>();


		if(medias != null && !medias.isEmpty()){
			for (MediaItem media : medias) {
				for (Tag tag : media.getTags()) {
					remainders.add(tag);
				}
			}
		}else{
			ITagService tagService = ServiceProvider.Resolve(ITagService.class);
			remainders = new HashSet<Tag>(tagService.getAllTags());
		}

		return remainders;
	}


	/**
	 * 
	 * @param selectedtype
	 * @param query
	 * @param selectedOrder
	 * @param requiredTags
	 * @param reverseOrder
	 * @return
	 */
	public Query buildMediadataCriteria(
			MediaType selectedtype, 
			String query,
			OrderProperty selectedOrder,
			List<Tag> requiredTags,
			List<MediaLibrary> requiredMediaLibs,
			boolean reverseOrder){

		//
		// build the query
		//

		Query q = SessionManager.getObjectContainer().query();
		q.constrain(MediaItem.class);

		// Limit results to requested media types:

		if(selectedtype != MediaType.ANY){
			q.descend("mediaType").constrain(selectedtype);
		}


		// Limit results to requested media libraries

		Constraint mediaLibConstraint = null;
		for (MediaLibrary mediaLib : requiredMediaLibs) {
			Constraint c = q.descend("sources").descend("parentLibrary").constrain(mediaLib);
			if(mediaLibConstraint == null)
				mediaLibConstraint = c;
			else
				mediaLibConstraint.or(c);
		}


		// Query for search field
		if(!query.isEmpty()){
			// We match the query against the media name, tags etc.

			// Predicate for matching the input with the filename

			Constraint constrFileName = q.descend("filename").constrain(query.toLowerCase()).contains();	

			//each tag which contains the input gets added to the the list tagsSearchBox.
			//the predicates are determined by checking if current mediaData item has the matched tag assigned
			for(Tag matchedTag : getMatchingTags(query)){
				constrFileName.or(q.descend("tags").constrain(matchedTag));
			}

		}


		for (Tag tag : requiredTags) {
			q.descend("tags").constrain(tag);
		}


		// Ordering

		if(reverseOrder){
			if(OrderProperty.FILENAME != selectedOrder)
			{
				q.descend(selectedOrder.getProperty()).orderAscending();
			}
			q.descend(OrderProperty.FILENAME.getProperty()).orderDescending();
		}else {
			if(OrderProperty.FILENAME != selectedOrder)
			{
				q.descend(selectedOrder.getProperty()).orderDescending();
			}
			q.descend(OrderProperty.FILENAME.getProperty()).orderAscending();
		}

		return q;
	}

	/**
	 * Builds a criteria which matches all media items with the given tag
	 * @param tag
	 * @return
	 */
	public static Query buildMediadataCriteria(Tag tag){
		ObjectContainer db = SessionManager.getObjectContainer();

		Query query = db.query();
		query.constrain(MediaItem.class);
		query.descend("tags").constrain(tag);

		return query;
	}

	/**
	 * Returns a list of tags which contain the query string in their names
	 * 
	 * @param query
	 * @return
	 */
	private List<Tag> getMatchingTags(String query){
		List<Tag> matchingTags = new ArrayList<Tag>();
		List<Tag> allTags = tagService.getAllTags();

		for(Tag tag : allTags){
			String tagName = tag.getName().toLowerCase();
			query = query.toLowerCase();

			if(tagName.contains(query))
				matchingTags.add(tag);
		}
		for (Tag tag : matchingTags) {
			System.out.println("matched Tags: " + tag.toString());
		}
		return matchingTags;
	}
}
