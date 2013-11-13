package vidada.model.filters;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;

/**
 * Represents the filter model 
 * @author IsNull
 *
 */
public interface IMediaFilter {

	/**
	 * Raised then the filter settings have been changed
	 * @return
	 */
	public abstract EventHandlerEx<EventArgs> getFilterChangedEvent();





}
