package vidada.viewsFX.libraries;

import archimedes.core.io.locations.DirectoryLocation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import vidada.IVidadaServer;
import vidada.client.viewmodel.MediaLibraryVM;
import vidada.model.media.MediaLibrary;
import vidada.model.settings.VidadaClientSettings;
import vidada.server.services.IMediaLibraryService;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Created by IsNull on 21.03.14.
 */
public class LibraryManagerController {

    private final IVidadaServer localServer = vidada.Application.getLocalServer();
    private final IMediaLibraryService libraryService = localServer.getLibraryService();

    private final ObservableList<MediaLibraryVM> libraries =  FXCollections.observableArrayList();

    @FXML
    private ListView<MediaLibraryVM> libraryView;

    private boolean hasChanges = false;


    public LibraryManagerController(){
        libraryService.getLibraryAddedEvent().add((s,e) -> libraries.add(new MediaLibraryVM(e.getValue())));
        libraryService.getLibraryRemovedEvent().add((s,e) -> libraries.remove(new MediaLibraryVM(e.getValue())));

        libraries.addAll(
                libraryService.getAllLibraries()
                .stream()
                .map(MediaLibraryVM::new)
                .collect(Collectors.toList()));
    }


    public ObservableList<MediaLibraryVM> getLibraries(){
        return libraries;
    }

    @FXML
    public void addNewLibrary(){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chose the root folder of your media library");

        final File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {

            DirectoryLocation location = DirectoryLocation.Factory.create(selectedDirectory.toURI());
            VidadaClientSettings settings = VidadaClientSettings.instance();

            MediaLibrary newLibrary = new MediaLibrary();
            newLibrary.setIgnoreImages(settings.isIgnoreImages());
            newLibrary.setIgnoreMovies(settings.isIgnoreMovies());
            newLibrary.setLibraryRoot(location);

            libraryService.addLibrary(newLibrary);

            hasChanges = true;
        }
    }

    @FXML
    public void removeSelectedLibrary(){
        MediaLibrary library = getSelectedLibrary();
        if(library != null){
            libraryService.removeLibrary(library);
            hasChanges = true;
        }
    }


    /**
     * Any changes to the libraries?
     * @return
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    public MediaLibrary getSelectedLibrary() {
        MediaLibraryVM vm = libraryView.getSelectionModel().getSelectedItem();
        return vm != null ? vm.getModel() : null;
    }

}
