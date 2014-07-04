package vidada.viewmodels;

import vidada.client.viewmodel.explorer.MediaExplorerVM;

/**
 *
 */
public class MainViewModel {


    private final MediaExplorerVM mediaExplorerVM;
    private final PrimaryMediaBrowserVM primaryMediaBrowserVM;

    public MainViewModel() {

        mediaExplorerVM = new MediaExplorerVM();
        primaryMediaBrowserVM = new PrimaryMediaBrowserVM();
    }


    public MediaExplorerVM getMediaExplorerVM() {
        return mediaExplorerVM;
    }

    public PrimaryMediaBrowserVM getPrimaryMediaBrowserVM() {
        return primaryMediaBrowserVM;
    }
}
