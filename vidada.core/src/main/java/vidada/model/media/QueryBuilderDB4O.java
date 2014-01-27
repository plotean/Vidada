package vidada.model.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.ServiceProvider;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;

import com.db4o.ObjectContainer;
import com.db4o.query.Constraint;
import com.db4o.query.Query;

/**
 * Builds a db4o media query from the generic media query object
 * 
 * @author IsNull
 *
 */
class QueryBuilderDB4O {

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
	public Query buildMediadataCriteria(MediaQuery query){

		//
		// build the query
		//

		Query q = SessionManagerDB4O.getObjectContainer().query();
		q.constrain(MediaItem.class);

		// Limit results to requested media types:

		if(query.getSelectedtype() != MediaType.ANY){
			q.descend("mediaType").constrain(query.getSelectedtype());
		}


		// Limit results to requested media libraries

		/* TODO: 
		 List<MediaLibrary> requiredMediaLibs = mediaLibraryService.getAllLibraries();
		if(onlyAvaiable)
		{
			for (MediaLibrary mediaLibrary : new ArrayList<MediaLibrary>(requiredMediaLibs)) {
				if(!mediaLibrary.isAvailable())
				{
					requiredMediaLibs.remove(mediaLibrary);
				}
			}
		}



		Constraint mediaLibConstraint = null;
		for (MediaLibrary mediaLib : requiredMediaLibs) {
			Constraint c = q.descend("sources").descend("parentLibrary").constrain(mediaLib);
			if(mediaLibConstraint == null)
				mediaLibConstraint = c;
			else
				mediaLibConstraint.or(c);
		}*/


		// Query for search field
		if(query.getKeywords() != null && !query.getKeywords().isEmpty()){
			// We match the query against the media name, tags etc.

			// Predicate for matching the input with the filename

			Constraint constrFileName = q.descend("filename").constrain(query.getKeywords().toLowerCase()).like();	

			//each tag which contains the input gets added to the the list tagsSearchBox.
			//the predicates are determined by checking if current mediaData item has the matched tag assigned
			for(Tag matchedTag : getMatchingTags(query.getKeywords())){
				constrFileName.or(q.descend("tags").constrain(matchedTag));
			}

		}


		for (Tag tag : query.getRequiredTags()) {
			q.descend("tags").constrain(tag);
		}

		for (Tag tag : query.getBlockedTags()) {
			q.descend("tags").constrain(tag).not();
		}


		// Ordering
		OrderProperty selectedOrder = query.getSelectedOrder();
		selectedOrder = selectedOrder == null ? OrderProperty.FILENAME : selectedOrder;
		if(query.isReverseOrder()){
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
	public Query buildMediadataCriteria(Tag tag){
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();

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
