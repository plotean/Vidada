package vidada.viewsFX.mediabrowsers;


import archimedes.core.data.IDeferLoaded;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.services.ISelectionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.GridView;
import vidada.client.model.browser.BrowserFolderItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.viewsFX.player.IMediaPlayerService;
import vidada.viewsFX.player.MediaPlayerService;
import vidada.viewsFX.util.ObservableListFXAdapter;

/**
 * Represents the media browser
 * @author IsNull
 *
 */
public class MediaBrowserFX extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaBrowserFX.class.getName());

    private final double defaultItemWidth = 200;
    private final double defaultItemHeight = 140;
    private final double itemAspectRatio = defaultItemWidth / defaultItemHeight;

    private MediaBrowserModel mediaModel;
    private GridView<IDeferLoaded<BrowserItemVM>> gridView;
    private GridViewViewPort gridViewPort;
    private IndexRange visibleCells;
    private ProgressIndicator loadingProgress;

    //transient private final ObservableList<BrowserItemVM> observableMedias;
    transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();

    private EventListenerEx<EventArgs> mediasChangedEventListener = (sender, eventArgs) -> updateView();


    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new media browser view
     */
    public MediaBrowserFX(){
        initView();
        loadingProgress.setVisible(false);
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Set the browser item width / size. The height is calculated from the aspect ratio.
     * @param width
     */
    public void setItemSize(double width){
        setItemSize(width, width / itemAspectRatio);
    }

    /**
     * Set the model data for this view
     * @param mediaModel
     */
    public void setDataContext(MediaBrowserModel mediaModel){

        logger.debug("Setting new MediaBrowserModel data context: " + mediaModel);

        if(this.mediaModel != null){
            this.mediaModel.getMediaChangedEvent().remove(mediasChangedEventListener);
        }

        this.mediaModel = mediaModel;

        if(mediaModel != null){
            mediaModel.getMediaChangedEvent().add(mediasChangedEventListener);
        }

        logger.debug("New media model events registered, now updating view...");

        updateView();
    }

    /**
     * Gets the {@link ISelectionManager} for the browser items.
     * @return
     */
    public ISelectionManager<IBrowserItem> getSelectionManager() {
        return mediaModel.getSelectionManager();
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Initializes the view
     */
    private void initView(){
        gridView = new GridView<>();

        gridViewPort = new GridViewViewPort(gridView);
        gridViewPort.getViewPortItemsChanged().add((sender, eventArgs) -> onVisibleItemsChanged());

        gridView.setStyle("-fx-background-color: #FFFFFF;");

        gridView.getStylesheets().add("css/default_media_cell.css");

        gridView.setCellFactory(control -> new MediaGridItemCell(
                mediaPlayerService,
                gridView.cellWidthProperty(),
                gridView.cellHeightProperty()
        ));

        gridView.setOnZoom(event -> {
            setItemSize(gridView.getCellWidth() * event.getZoomFactor());
            event.consume();
        });

        setItemSize(200, 140);

        loadingProgress = new ProgressIndicator();
        loadingProgress.setMaxWidth(70);

        StackPane mediaGridStack = new StackPane();
        mediaGridStack.getChildren().addAll(gridView, loadingProgress);
        this.setCenter(mediaGridStack);
    }

    /**
     * Updates the browser items
     */
    private synchronized void updateView(){
        Platform.runLater(() -> {
            // Run the following code in the JavaFX UI Thread soon

            logger.debug("Updating view...");

            ObservableList<IDeferLoaded<BrowserItemVM>> observableMedias;

            if (mediaModel != null && mediaModel.getMedias() != null) {
                observableMedias = new ObservableListFXAdapter<>(mediaModel.getMedias());
            } else {
                observableMedias = FXCollections.observableArrayList();
                logger.debug("Medias empty, nothing to show.");
            }

            gridView.setItems(observableMedias);
            gridViewPort.ensureViewportChangedListener(); // TODO HACK: (Should be called once after Scene is shown)

            logger.debug("Updating view done.");
        });
    }

    /**
     * Set the item size
     * @param width
     * @param height
     */
    private void setItemSize(double width, double height){
        gridView.setCellWidth(width);
        gridView.setCellHeight(height);
    }

    /**
     * Occurs when the items visible in the current ViewPort have been changed.
     */
    private void onVisibleItemsChanged(){

        IndexRange currentVisibleCells = gridViewPort.getVisibleCellRange();
        int[] dropped = IndexRange.unused(visibleCells, currentVisibleCells);

        ObservableList<IDeferLoaded<BrowserItemVM>> observableMedias = gridView.getItems();

        if(dropped.length != 0){
            for (int i : dropped) {
                if(observableMedias.size() > i){
                    IDeferLoaded<BrowserItemVM> droppedVM = observableMedias.get(i);
                    if(droppedVM.isLoaded())
                        droppedVM.getLoadedItem().outsideViewPort();
                }
            }
        }

        visibleCells = currentVisibleCells;
    }

    /**
     * Occurs when a folderItem was opened
     * TODO Browser?
     * @param folderItem
     */
    protected void onItemFolderOpen(BrowserFolderItem folderItem){
        //setDataContext(folderItem);
    }


}
