package vidada.viewsFX.mediaexplorer;


import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.controlsfx.control.breadcrumbs.BreadCrumbBar;
import org.controlsfx.control.breadcrumbs.BreadCrumbBar.BreadCrumbActionEvent;
import org.controlsfx.control.breadcrumbs.BreadCrumbButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import vidada.viewmodel.explorer.MediaExplorerVM;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoryLocation;

public class PrimaryMediaExplorerFX extends BorderPane {
	private final MediaBrowserFX mediaBrowserFX;
	private final ExplorerFilterFX filterView;
	private final BreadCrumbBar<LocationBreadCrumb> breadCrumbBar;

	private final BreadCrumbNavigationDecorator breadCrumbModel;
	private MediaExplorerVM explorerViewModel;

	private final Node homeView;


	public PrimaryMediaExplorerFX(){


		GlyphFont font = GlyphFontRegistry.font("FontAwesome");
		homeView = font.fontSize(16).create(FontAwesome.Glyph.HOME.name());

		// Browser View
		mediaBrowserFX = new MediaBrowserFX();

		this.setCenter(mediaBrowserFX);
		//this.setCenter(mediaBrowserFX);

		// Filter View
		filterView = new ExplorerFilterFX();

		// Navigation / Breadcrumb
		breadCrumbBar = createNavigation();
		breadCrumbModel = new BreadCrumbNavigationDecorator(breadCrumbBar, new HomeLocationBreadCrumb());

		VBox topBox = new VBox();
		topBox.getChildren().add(filterView);
		topBox.getChildren().add(breadCrumbBar);

		TitledPane filterPane = new TitledPane("Source", topBox);
		this.setTop(filterPane);
	}


	private BreadCrumbBar<LocationBreadCrumb> createNavigation(){

		BreadCrumbBar<LocationBreadCrumb> bar = new BreadCrumbBar<LocationBreadCrumb>();


		bar.setCrumbFactory(new Callback<TreeItem<LocationBreadCrumb>, BreadCrumbButton>() {

			@Override
			public BreadCrumbButton call(TreeItem<LocationBreadCrumb> crumbModel) {
				BreadCrumbButton crumbView = null;
				if(crumbModel.getValue() instanceof HomeLocationBreadCrumb){
					crumbView = new HomeBreadcrumbButton("", homeView);
				}else{
					crumbView = new BreadCrumbButton(crumbModel.getValue().getName());
				}
				return crumbView;
			}
		});


		bar.setOnCrumbAction(new EventHandler<BreadCrumbBar.BreadCrumbActionEvent<LocationBreadCrumb>>() {

			@Override
			public void handle(BreadCrumbActionEvent<LocationBreadCrumb> crumbArgs) {
				LocationBreadCrumb crumb = crumbArgs.getCrumbModel().getValue();

				if(crumb instanceof HomeLocationBreadCrumb){
					System.out.println("home pressed...");
				}else{
					DirectoryLocation dir = crumb.getDirectoryLocation();
					explorerViewModel.setCurrentLocation(dir);
					breadCrumbModel.setDirectory(breadCrumbModel.getHomeDirectory(), dir);
				}
			}
		});

		BorderPane.setMargin(bar, new Insets(10));

		return bar;
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
