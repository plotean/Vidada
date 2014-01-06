package vidada.viewsFX.mediaexplorer;


import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.viewmodel.explorer.MediaExplorerVM;
import vidada.viewsFX.breadcrumbs.BreadCrumbBar;
import vidada.viewsFX.breadcrumbs.BreadCrumbBarModel;
import vidada.viewsFX.breadcrumbs.SimpleBreadCrumbModel;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;

public class PrimaryMediaExplorerFX extends BorderPane {
	private final MediaBrowserFX mediaBrowserFX;
	private final ExplorerFilterFX filterView;

	private final BreadCrumbBarModel breadCrumbModel = new BreadCrumbBarModel();
	private MediaExplorerVM explorerViewModel;


	public PrimaryMediaExplorerFX(){


		breadCrumbModel.add(
				new SimpleBreadCrumbModel("Home"),
				new SimpleBreadCrumbModel("This"),
				new SimpleBreadCrumbModel("Is"),
				new SimpleBreadCrumbModel("A (small)"),
				new SimpleBreadCrumbModel("Folder"));


		// Browser View
		mediaBrowserFX = new MediaBrowserFX();

		this.setTop(createNavigation());

		this.setCenter(createNavigation());
		//this.setCenter(mediaBrowserFX);


		// Filter View
		filterView = new ExplorerFilterFX();
		TitledPane filterPane = new TitledPane("Source", filterView);
		this.setTop(filterPane);
	}


	private Node createNavigation(){
		BreadCrumbBar breadCrumbBar = new BreadCrumbBar();
		breadCrumbBar.setDataContext(breadCrumbModel);
		return breadCrumbBar;
	}


	public void setDataContext(MediaExplorerVM explorerViewModel){
		if(this.explorerViewModel != null)
			this.explorerViewModel.getBrowserModelChangedEvent().remove(browserModelChangedListener);

		this.explorerViewModel = explorerViewModel;

		if(explorerViewModel != null){
			explorerViewModel.getBrowserModelChangedEvent().add(browserModelChangedListener);
		}

		updateBrowserModel();
	}

	private final EventListenerEx<EventArgs> browserModelChangedListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			updateBrowserModel();
		}
	};


	private void updateBrowserModel(){
		if(explorerViewModel != null){
			mediaBrowserFX.setDataContext(explorerViewModel.getBrowserModel());
			filterView.setDataContext(explorerViewModel);
		}else{
			mediaBrowserFX.setDataContext(null);
			filterView.setDataContext(null);
		}
	}

}
