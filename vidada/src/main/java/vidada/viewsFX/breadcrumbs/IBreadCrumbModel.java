package vidada.viewsFX.breadcrumbs;

import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;

/**
 * Model of a single BredCrumb
 * @author IsNull
 *
 */
public interface IBreadCrumbModel {

	/**
	 * Raised when this bread crumb is opened
	 * @return
	 */
	public IEvent<EventArgsG<IBreadCrumbModel>> getOpenEvent();


	/**
	 * Get the display name of the button
	 * @return
	 */
	public String getName();

	/**
	 * Occurs when this breadcrumb is opened
	 */
	public void open();
}
