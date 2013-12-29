package vidada.viewsFX.mediaexplorer;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.model.browser.MediaBrowserModel;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;

public class PrimaryMediaExplorerFX extends BorderPane {
	private final MediaBrowserModel browserModel;

	public PrimaryMediaExplorerFX(){

		browserModel = new MediaBrowserModel();

		// Browser View
		MediaBrowserFX mediaBrowserFX = new MediaBrowserFX();
		mediaBrowserFX.setDataContext(browserModel);
		this.setCenter(mediaBrowserFX);

		// Filter View
		ExplorerFilterFX filterView = new ExplorerFilterFX();
		TitledPane filterPane = new TitledPane("Source", filterView);
		this.setTop(filterPane);

	}
}
