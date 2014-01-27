package vidada.viewsFX.mediaexplorer;

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
import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.store.libraries.IMediaLibraryService;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.media.store.local.LocalMediaStore;
import vidada.viewmodel.explorer.MediaExplorerVM;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoryLocation;

public class ExplorerFilterFX extends BorderPane {

	private final ComboBox<MediaLibrary> cboMediaLibrary= new ComboBox<>();
	private final Label libraryDescription = new Label("Media Library:");
	private final ObservableList<MediaLibrary> observableMedias;

	private final LocalMediaStore localStore = ServiceProvider.Resolve(IMediaService.class).getLocalMediaStore();
	private final IMediaLibraryService mediaLibraryService = localStore.getLibraryManager();


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
			public void changed(ObservableValue<? extends MediaLibrary> obs, MediaLibrary arg1, MediaLibrary arg2) {

				MediaLibrary currentLibrary = cboMediaLibrary.getValue();
				if(currentLibrary != null){
					DirectoryLocation dir = currentLibrary.getMediaDirectory().getDirectory();
					if(mediaExplorerVm != null){
						mediaExplorerVm.setHomeLocation(dir);
					}else
						System.err.println("ExplorerFilterFX: mediaExplorerVm is NULL!");
				}
			}

		});

		mediaLibraryService.getLibraryAddedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, final EventArgsG<MediaLibrary> eventArgs) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						observableMedias.add(eventArgs.getValue());
					}
				});

			}
		});

		mediaLibraryService.getLibraryRemovedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, final EventArgsG<MediaLibrary> eventArgs) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						observableMedias.remove(eventArgs.getValue());
					}
				});
			}
		});

		setCenter(box);
	}


	public void setDataContext(MediaExplorerVM mediaExplorerVm){
		this.mediaExplorerVm = mediaExplorerVm;
	}

}
