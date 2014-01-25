package vidada.model.media;

import java.util.List;

import vidada.model.tags.Tag;

/**
 * Represents an generic media query.
 * 
 * This query will be translated into a storage specific query and the executed
 * @author IsNull
 *
 */
public class MediaQuery {

	private MediaType selectedtype;
	private String keywords;
	private OrderProperty selectedOrder;
	private List<Tag> requiredTags;
	private List<Tag> blockedTags;
	private boolean onlyAvailable;
	private boolean reverseOrder;


	public MediaQuery() {

	}

	/**
	 * Creates a media query with the following 
	 * @param selectedtype
	 * @param keywords
	 * @param selectedOrder
	 * @param requiredTags
	 * @param blockedTags
	 * @param requiredMediaLibs
	 * @param reverseOrder
	 */
	public MediaQuery(MediaType selectedtype, String keywords,
			OrderProperty selectedOrder, List<Tag> requiredTags,
			List<Tag> blockedTags, boolean onlyAvailable,
			boolean reverseOrder) {
		super();
		this.selectedtype = selectedtype;
		this.keywords = keywords;
		this.selectedOrder = selectedOrder;
		this.requiredTags = requiredTags;
		this.blockedTags = blockedTags;
		this.setOnlyAvailable(onlyAvailable);
		this.reverseOrder = reverseOrder;
	}


	public MediaType getSelectedtype() {
		return selectedtype;
	}

	public String getKeywords() {
		return keywords;
	}

	public OrderProperty getSelectedOrder() {
		return selectedOrder;
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

	public void setSelectedOrder(OrderProperty selectedOrder) {
		this.selectedOrder = selectedOrder;
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
