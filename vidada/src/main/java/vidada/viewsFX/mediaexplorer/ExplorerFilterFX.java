package vidada.viewsFX.mediaexplorer;

import archimedes.core.io.locations.DirectoryLocation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.IVidadaServer;
import vidada.client.viewmodel.explorer.MediaExplorerVM;
import vidada.model.media.MediaLibrary;
import vidada.server.services.IMediaLibraryService;

public class ExplorerFilterFX extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(ExplorerFilterFX.class.getName());


    private final ComboBox<MediaLibrary> cboMediaLibrary= new ComboBox<>();
	private final Label libraryDescription = new Label("Media Library:");
	private ObservableList<MediaLibrary> observableMedias;

	private final IVidadaServer localServer = vidada.Application.getLocalServer();
	private IMediaLibraryService mediaLibraryService;


	private MediaExplorerVM mediaExplorerVm;


    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ExplorerFilterFX
     */
	public ExplorerFilterFX(){

		if(localServer != null){
			mediaLibraryService = localServer.getLibraryService();

		}

		this.setPadding(new Insets(10));

		Insets margrin = new Insets(5,5,10,0);
		HBox.setMargin(libraryDescription, margrin);
		HBox.setMargin(cboMediaLibrary, margrin);

		HBox box = new HBox();
		box.getChildren().add(libraryDescription);
		box.getChildren().add(cboMediaLibrary);

		cboMediaLibrary.valueProperty().addListener(new ChangeListener<MediaLibrary>() {

			@Override
			public void changed(ObservableValue<? extends MediaLibrary> obs, MediaLibrary arg1, MediaLibrary arg2) {

				MediaLibrary currentLibrary = cboMediaLibrary.getValue();
				if(currentLibrary != null){
					DirectoryLocation dir = currentLibrary.getMediaDirectory().getDirectory();
					if(mediaExplorerVm != null){
						mediaExplorerVm.setHomeLocation(dir);
					}else
                        logger.error("ExplorerFilterFX: mediaExplorerVm is NULL!");
				}
			}

		});

		if(mediaLibraryService != null){

			observableMedias = FXCollections.observableArrayList(mediaLibraryService.getAllLibraries());
			cboMediaLibrary.setItems(observableMedias);

			mediaLibraryService.getLibraryAddedEvent().add((sender, eventArgs) -> Platform.runLater(() -> observableMedias.add(eventArgs.getValue())));

			mediaLibraryService.getLibraryRemovedEvent().add((sender, eventArgs) -> Platform.runLater(() -> observableMedias.remove(eventArgs.getValue())));
		}

		setCenter(box);
	}


	public void setDataContext(MediaExplorerVM mediaExplorerVm){
		this.mediaExplorerVm = mediaExplorerVm;
	}

}
