package vidada.viewsFX.mediaexplorer;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vidada.viewmodel.explorer.MediaExplorerVM;
import vidada.viewsFX.breadcrumbs.BreadCrumbBar;
import vidada.viewsFX.breadcrumbs.IBreadCrumbModel;
import vidada.viewsFX.breadcrumbs.SimpleBreadCrumbModel;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;

public class PrimaryMediaExplorerFX extends BorderPane {
	private final MediaBrowserFX mediaBrowserFX;
	private final ExplorerFilterFX filterView;

	private final LocationBreadCrumbBarModel breadCrumbModel = new LocationBreadCrumbBarModel();
	private MediaExplorerVM explorerViewModel;


	public PrimaryMediaExplorerFX(){

		breadCrumbModel.add(
				new SimpleBreadCrumbModel("Home"),
				new SimpleBreadCrumbModel("This"),
				new SimpleBreadCrumbModel("Is"),
				new SimpleBreadCrumbModel("A (small)"),
				new SimpleBreadCrumbModel("Folder"));

		breadCrumbModel.getBreadCrumbOpenEvent().add(new EventListenerEx<EventArgsG<IBreadCrumbModel>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<IBreadCrumbModel> eventArgs) {
				if(eventArgs.getValue() instanceof LocationBreadCrumb){
					LocationBreadCrumb crumb = (LocationBreadCrumb)eventArgs.getValue();
					explorerViewModel.setCurrentLocation(crumb.getDirectoryLocation());
				}else{
					System.err.println("PrimaryMediaExplorerFX: Breadcrumb model has unexpected children: " + eventArgs.getValue());
				}
			}
		});


		// Browser View
		mediaBrowserFX = new MediaBrowserFX();

		this.setCenter(mediaBrowserFX);
		//this.setCenter(mediaBrowserFX);

		// Filter View
		filterView = new ExplorerFilterFX();

		// Navigation / Breadcrumb
		Node navNode = createNavigation();

		VBox topBox = new VBox();
		topBox.getChildren().add(filterView);
		topBox.getChildren().add(navNode);

		TitledPane filterPane = new TitledPane("Source", topBox);
		this.setTop(filterPane);
	}


	private Node createNavigation(){

		BreadCrumbBar breadCrumbBar = new BreadCrumbBar();
		breadCrumbBar.setDataContext(breadCrumbModel);
		BorderPane.setMargin(breadCrumbBar, new Insets(10));

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

		System.out.println("PrimaryMediaExplorerFX updateBrowserModel!");

		if(explorerViewModel != null){
			mediaBrowserFX.setDataContext(explorerViewModel.getBrowserModel());
			filterView.setDataContext(explorerViewModel);
			breadCrumbModel.setDirectory(explorerViewModel.getHomeLocation(), explorerViewModel.getCurrentDirectory());
		}else{
			mediaBrowserFX.setDataContext(null);
			filterView.setDataContext(null);
			breadCrumbModel.setDirectory(null, null);
		}
	}

}
