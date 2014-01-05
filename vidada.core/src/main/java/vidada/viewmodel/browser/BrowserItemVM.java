package vidada.viewmodel.browser;

import vidada.model.browser.IBrowserItem;
import vidada.viewmodel.IViewModel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;


public abstract class BrowserItemVM implements IViewModel<IBrowserItem>{
	private boolean isSelected = false;
	private IBrowserItem item;

	public final EventHandlerEx<EventArgs> SelectionChangedEvent = new EventHandlerEx<EventArgs>();


	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		if(this.isSelected != isSelected){
			this.isSelected = isSelected;
			SelectionChangedEvent.fireEvent(this, EventArgs.Empty);
		}
	}

	/**
	 * Default open action
	 * @return
	 */
	public abstract boolean open();

	/**
	 * Method is called when this media item is outside the view port
	 */
	public abstract void outsideViewPort();

	public String getName(){
		return item != null ? item.getName() : "<null>";
	}

	@Override
	public IBrowserItem getModel() {
		return item;
	}


	@Override
	public void setModel(IBrowserItem model) {
		item = model;
	}

}
