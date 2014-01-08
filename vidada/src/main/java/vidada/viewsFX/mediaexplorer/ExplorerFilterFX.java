package vidada.viewsFX.mediaexplorer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.viewmodel.explorer.MediaExplorerVM;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoryLocation;

public class ExplorerFilterFX extends BorderPane {

	private final ComboBox<MediaLibrary> cboMediaLibrary= new ComboBox<>();
	private final Label libraryDescription = new Label("Media Library:");
	private final ObservableList<MediaLibrary> observableMedias;

	private final IMediaLibraryService mediaLibraryService = ServiceProvider.Resolve(IMediaLibraryService.class);


	private MediaExplorerVM mediaExplorerVm;

	public ExplorerFilterFX(){

		this.setPadding(new Insets(10));

		Insets margrin = new Insets(5,5,10,0);
		HBox.setMargin(libraryDescription, margrin);
		HBox.setMargin(cboMediaLibrary, margrin);

		HBox box = new HBox();
		box.getChildren().add(libraryDescription);
		box.getChildren().add(cboMediaLibrary);


		observableMedias = FXCollections.observableArrayList(mediaLibraryService.getAllLibraries());
		cboMediaLibrary.setItems(observableMedias);

		cboMediaLibrary.valueProperty().addListener(new ChangeListener<MediaLibrary>() {
			@Override 
			public void changed(ObservableValue ov, MediaLibrary t, MediaLibrary t1) {                
				MediaLibrary currentLibrary = cboMediaLibrary.getValue();
				DirectoryLocation dir = currentLibrary.getMediaDirectory().getDirectory();
				if(mediaExplorerVm != null){
					mediaExplorerVm.setHomeLocation(dir);
				}else
					System.err.println("ExplorerFilterFX: mediaExplorerVm is NULL!");
			}    
		});

		mediaLibraryService.getLibraryAddedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				observableMedias.add(eventArgs.getValue());
			}
		});

		mediaLibraryService.getLibraryRemovedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				observableMedias.remove(eventArgs.getValue());
			}
		});

		setCenter(box);
	}


	public void setDataContext(MediaExplorerVM mediaExplorerVm){
		this.mediaExplorerVm = mediaExplorerVm;
	}

}
