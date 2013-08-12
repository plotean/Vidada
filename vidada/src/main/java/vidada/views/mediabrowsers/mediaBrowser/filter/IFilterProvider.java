package vidada.views.mediabrowsers.mediaBrowser.filter;

import java.util.List;

import vidada.model.media.MediaItem;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;

import com.db4o.query.Query;


/**
 * Provides a filter and a change notifier when the filter has been changed.
 * @author IsNull
 *
 */
public interface IFilterProvider {

	public abstract EventHandlerEx<EventArgs> getFilterChangedEvent();

	/**
	 * Gets the criteria which will return the medias 
	 * accoding to the filter settings
	 * @return
	 */
	public abstract Query getCriteria();


	/**
	 * Set the current result set.
	 * The filter will adjust the possible (remaining) filter options
	 * @param medias
	 */
	public abstract void setCurrentResultSet(List<MediaItem> medias);
	//public abstract Set<Tag> currentRequiredTags();

}