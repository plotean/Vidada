package vidada.viewsFX.mediabrowsers;


import archimedes.core.data.IDeferLoaded;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.services.ISelectionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
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

    private MediaBrowserModel mediaModel;
    private GridView<IDeferLoaded<BrowserItemVM>> gridView;
    private GridViewViewPort gridViewPort;
    private IndexRange visibleCells;


    //transient private final ObservableList<BrowserItemVM> observableMedias;
    transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


    public MediaBrowserFX(){
        initView();
    }

    private final double defaultItemWidth = 200;
    private final double defaultItemHeight = 140;
    private final double itemAspectRatio = defaultItemWidth / defaultItemHeight;

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

        setItemSize(200, 140);

        this.setCenter(gridView);


        gridView.setOnZoom(event -> {
            //System.out.println("zoom factor: " + event.getZoomFactor());
            setItemSize(gridView.getCellWidth() * event.getZoomFactor());
            event.consume();
        });


    }


    public void setItemSize(double width, double height){
        gridView.setCellWidth(width);
        gridView.setCellHeight(height);
    }

    public void setItemSize(double width){
        setItemSize(width, width / itemAspectRatio);
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

    public void setDataContext(MediaBrowserModel mediaModel){

        logger.debug("MediaBrowserFX: setDataContext " + mediaModel);

        if(this.mediaModel != null){
            this.mediaModel.getMediaChangedEvent().remove(mediasChangedEventListener);
        }

        this.mediaModel = mediaModel;

        if(mediaModel != null){
            mediaModel.getMediaChangedEvent().add(mediasChangedEventListener);
        }

        updateView();
    }

    transient private EventListenerEx<EventArgs> mediasChangedEventListener = (sender, eventArgs) -> updateView();

    /**
     * Occurs when a folderItem was opened
     * @param folderItem
     */
    protected void onItemFolderOpen(BrowserFolderItem folderItem){
        //setDataContext(folderItem);
    }

    private synchronized void updateView(){
        Platform.runLater(() -> {
            ObservableList<IDeferLoaded<BrowserItemVM>> observableMedias;

            if(mediaModel != null && mediaModel.getMedias() != null){
                observableMedias = new ObservableListFXAdapter<>(mediaModel.getMedias());
            }else {
                observableMedias = FXCollections.observableArrayList();
                logger.debug("Medias empty, nothing to show.");
            }

            gridView.setItems(observableMedias);
            gridViewPort.ensureViewportChangedListener(); // TODO HACK: (Should be called once after Scene is shown)
        });
    }

    public ISelectionManager<IBrowserItem> getSelectionManager() {
        return mediaModel.getSelectionManager();
    }

}
