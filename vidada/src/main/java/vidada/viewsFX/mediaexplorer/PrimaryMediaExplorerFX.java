package vidada.viewsFX.mediaexplorer;


import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.io.locations.DirectoryLocation;
import impl.org.controlsfx.skin.BreadCrumbBarSkin.BreadCrumbButton;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import vidada.client.viewmodel.explorer.MediaExplorerVM;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;

public class PrimaryMediaExplorerFX extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(PrimaryMediaExplorerFX.class.getName());

	private final MediaBrowserFX mediaBrowserFX;
	private final ExplorerFilterFX filterView;
	private final BreadCrumbBar<LocationBreadCrumb> breadCrumbBar;

	private final BreadCrumbNavigationDecorator breadCrumbModel;
	private MediaExplorerVM explorerViewModel;

	private final Node homeView;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new PrimaryMediaExplorerFX
     */
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

		BreadCrumbBar<LocationBreadCrumb> bar = new BreadCrumbBar<>();


		bar.setCrumbFactory(crumbModel -> {
            BreadCrumbButton crumbView = null;
            if(crumbModel.getValue() instanceof HomeLocationBreadCrumb){
                crumbView = new HomeBreadcrumbButton("", homeView);
            }else{
                crumbView = new BreadCrumbButton(crumbModel.getValue().getName());
            }
            return crumbView;
        });


		bar.setOnCrumbAction(crumbArgs -> {
            LocationBreadCrumb crumb = crumbArgs.getSelectedCrumb().getValue();

            if(crumb instanceof HomeLocationBreadCrumb){
                System.out.println("home pressed...");
            }else{
                DirectoryLocation dir = crumb.getDirectoryLocation();
                explorerViewModel.setCurrentLocation(dir);
                breadCrumbModel.setDirectory(breadCrumbModel.getHomeDirectory(), dir);
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

	private final EventListenerEx<EventArgs> browserModelChangedListener = (sender, eventArgs) -> updateBrowserModel();


	private void updateBrowserModel(){

		logger.debug("PrimaryMediaExplorerFX updateBrowserModel!");

		if(explorerViewModel != null){
			// TODO update Browser Model
			//mediaBrowserFX.setDataContext(explorerViewModel.getBrowserModel());
			filterView.setDataContext(explorerViewModel);
			breadCrumbModel.setDirectory(explorerViewModel.getHomeLocation(), explorerViewModel.getCurrentDirectory());
		}else{
			mediaBrowserFX.setDataContext(null);
			filterView.setDataContext(null);
			breadCrumbModel.setDirectory(null, null);
		}
	}

}
