package vidada.viewsFX;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.viewmodels.PrimaryMediaBrowserVM;
import vidada.viewsFX.filters.FilterViewFx;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import vidada.viewsFX.medias.MediaDetailController;

/**
 * Represents the main media browser view,
 * with a filter on top, a nice browser grid and a detail pane to edit.
 *
 *
 * @author IsNull
 *
 */
public class PrimaryMediaBrowserFX extends BorderPane {


    /**
     * Creates a PrimaryMediaBrowserFX View
     * @param primaryMediaBrowserVM
     */
	public PrimaryMediaBrowserFX(PrimaryMediaBrowserVM primaryMediaBrowserVM){

		// Browser View
		final MediaBrowserFX mediaBrowserFX = new MediaBrowserFX();
		mediaBrowserFX.setDataContext(primaryMediaBrowserVM.getBrowserModel());
		this.setCenter(mediaBrowserFX);

		// Filter View
		FilterViewFx filterView = new FilterViewFx(primaryMediaBrowserVM.getFilterModel());
		TitledPane filterPane = new TitledPane("Filter", filterView);
		this.setTop(filterPane);

		// Detail View
        final Node detailView = FXMLLoaderX.load("medias/MediaDetailView.fxml");
        final MediaDetailController mediaDetailController = (MediaDetailController)detailView.getUserData();

		TitledPane detailPane = new TitledPane("Detail", detailView);
		this.setBottom(detailPane);


        primaryMediaBrowserVM.getMediaDetailVMChanged().add((sender, eventArgs) -> {
            mediaDetailController.setDataContext(primaryMediaBrowserVM.getMediaDetailViewModel());
        });
	}




}
