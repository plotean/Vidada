package vidada.controller.filters;

import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import archimedesJ.events.EventArgs;
import archimedesJ.events.IEvent;

/**
 * Represents a filter view
 * @author IsNull
 *
 */
public interface IFilterView {

	/**
	 * Raised when the user has changed a filter in this view
	 * @return
	 */
	public abstract IEvent<EventArgs> getFilterChangedEvent();



	/**
	 * Gets the selected media type
	 * @return
	 */
	MediaType getSelectedMediaType();

	/**
	 * Returns the selected order
	 * @return
	 */
	OrderProperty getSelectedOrder();

	/**
	 * Returns true if Reverse is selected
	 * @return
	 */
	boolean isReverseOrder();

	/**
	 * Gets the user query string
	 * @return
	 */
	String getQueryString();

	/**
	 * Gets if only available medias should be returned
	 * @return
	 */
	boolean isOnlyShowAvaiable();

}
