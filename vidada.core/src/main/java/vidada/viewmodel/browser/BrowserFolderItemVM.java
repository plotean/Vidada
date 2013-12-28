package vidada.viewmodel.browser;

import vidada.model.browser.BrowserFolderItem;

public class BrowserFolderItemVM extends BrowserItemVM{

	private BrowserFolderItem model;

	public BrowserFolderItemVM(BrowserFolderItem model){
		setModel(model);
	}


	public String getName(){
		return model.getName();
	}

	@Override
	public BrowserFolderItem getModel() {
		return (BrowserFolderItem)super.getModel();
	}


	public void setModel(BrowserFolderItem model) {
		super.setModel(model);
		this.model = model;
	}

	@Override
	public void outsideViewPort() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString(){
		return getName();
	}
}
