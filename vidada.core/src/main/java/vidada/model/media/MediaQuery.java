package vidada.model.media;

import java.util.List;

import vidada.model.queries.AbstractQuery;
import vidada.model.tags.Tag;

/**
 * Represents an generic media query.
 * 
 * This query will be translated into a storage specific query and the executed
 * @author IsNull
 *
 */
public class MediaQuery extends AbstractQuery<MediaItem>{

	/**
	 * Represents a query which will match all entities
	 */
	public static final MediaQuery ALL = new MediaQuery(AbstractQuery.QueryType.All);

	private MediaType selectedtype = MediaType.ANY;
	private String keywords = null;
	private OrderProperty order = OrderProperty.FILENAME;
	private List<Tag> requiredTags = null;
	private List<Tag> blockedTags = null;
	private boolean onlyAvailable = false;
	private boolean reverseOrder = false;




	/**
	 * Creates a media query with the following 
	 * @param selectedtype
	 * @param keywords
	 * @param order
	 * @param requiredTags
	 * @param blockedTags
	 * @param requiredMediaLibs
	 * @param reverseOrder
	 */
	public MediaQuery(MediaType selectedtype, String keywords,
			OrderProperty order, List<Tag> requiredTags,
			List<Tag> blockedTags, boolean onlyAvailable,
			boolean reverseOrder) {
		this(AbstractQuery.QueryType.Query);
		this.selectedtype = selectedtype;
		this.keywords = keywords;
		this.order = order;
		this.requiredTags = requiredTags;
		this.blockedTags = blockedTags;
		this.setOnlyAvailable(onlyAvailable);
		this.reverseOrder = reverseOrder;
	}

	protected MediaQuery(AbstractQuery.QueryType type) {
		super(type, MediaItem.class);
	}


	public MediaType getSelectedtype() {
		return selectedtype;
	}

	public String getKeywords() {
		return keywords;
	}

	public OrderProperty getOrder() {
		return order;
	}

	public List<Tag> getRequiredTags() {
		return requiredTags;
	}

	public List<Tag> getBlockedTags() {
		return blockedTags;
	}

	public boolean isReverseOrder() {
		return reverseOrder;
	}

	public void setSelectedtype(MediaType selectedtype) {
		this.selectedtype = selectedtype;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setOrder(OrderProperty order) {
		this.order = order;
	}

	public void setRequiredTags(List<Tag> requiredTags) {
		this.requiredTags = requiredTags;
	}

	public void setBlockedTags(List<Tag> blockedTags) {
		this.blockedTags = blockedTags;
	}

	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}

	public boolean isOnlyAvailable() {
		return onlyAvailable;
	}

	public void setOnlyAvailable(boolean onlyAvailable) {
		this.onlyAvailable = onlyAvailable;
	}

}
