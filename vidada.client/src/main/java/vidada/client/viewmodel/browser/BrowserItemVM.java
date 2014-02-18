package vidada.client.viewmodel.browser;

import vidada.client.viewmodel.IViewModel;
import vidada.model.browser.IBrowserItem;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;


public class BrowserItemVM implements IViewModel<IBrowserItem>{
	private boolean isSelected = false;
	private IBrowserItem item;

	public final EventHandlerEx<EventArgs> SelectionChangedEvent = new EventHandlerEx<EventArgs>();

	public BrowserItemVM() {}

	public BrowserItemVM(IBrowserItem item) { 
		// Do not call any virtual methods here such as setModel!
		this.item = item; 
	}

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
	public boolean open() { return false; }

	/**
	 * Method is called when this media item is outside the view port
	 */
	public void outsideViewPort() {  }

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
