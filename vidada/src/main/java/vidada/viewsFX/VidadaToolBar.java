package vidada.viewsFX;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.viewsFX.ImageResources.IconType;
import vidada.viewsFX.dialoges.SettingsDialog;
import vidada.viewsFX.dialoges.SimpleDialog;
import vidada.viewsFX.dialoges.SynchronizeDialog;
import vidada.viewsFX.libraries.LibraryManagerController;
import vidada.viewsFX.update.UpdateBar;

public class VidadaToolBar extends HBox{

    private static final Logger logger = LogManager.getLogger(VidadaToolBar.class.getName());

    private static boolean autoSync = true;
    private static final String Style_ToolBar_Background = "-fx-background-color: #505050;";


    public VidadaToolBar(){

        this.setStyle(Style_ToolBar_Background);

        ToolBar toolBar = new ToolBar();
        createToolBar(toolBar);
        HBox.setHgrow(toolBar, Priority.ALWAYS);

        this.getChildren().addAll(toolBar, new UpdateBar());

    }

    private void createToolBar(ToolBar bar){
        bar.setStyle(Style_ToolBar_Background);

        bar.getItems().add(
                createToolBarButton(IconType.FOLDER_ICON_32, () -> {

                    Node libManager = FXMLLoaderX.load("libraries/LibraryManagerView.fxml");

                    SimpleDialog libraryDialog = new SimpleDialog(null, "Vidada Library Manager", libManager);
                    libraryDialog.setMasthead("Here you can add or remove media libraries.");
                    libraryDialog.show();

                    LibraryManagerController vm  = (LibraryManagerController)libManager.getUserData();

                    if(vm != null && vm.hasChanges()) {
                        boolean sync = true;
                        if(!autoSync) {
                            Action response = Dialogs.create()
                                    .title("Vidada - Libraries have changed")
                                    .masthead("Do you want to synchronize your media library?")
                                    .message("The media libraries have changed which means that your current media database needs to be updated. Do you want to update now?")
                                    .showConfirm();
                            sync = response == Dialog.Actions.YES;
                        }
                        if(sync){
                            UpdateMediaLibraryAction updateMediaLibraryAction = new UpdateMediaLibraryAction(null);
                            updateMediaLibraryAction.actionPerformed(null);
                        }
                    }

                }));

        bar.getItems().add(
                createToolBarButton(IconType.UPDATELIB_ICON_32, () -> Platform.runLater(() -> {
                    SynchronizeDialog scanUpdateDialog = new SynchronizeDialog(null);
                    scanUpdateDialog.show();
                })));

        bar.getItems().add(
                createToolBarButton(IconType.TAG_ICON_32, () -> {

                    logger.error("Tag manager not implemented!");
                }));

        bar.getItems().add(new Separator());

        bar.getItems().add(
                createToolBarButton(IconType.SETTINGS_ICON_32, () -> {
                    SettingsDialog dlg = new SettingsDialog(null);
                    dlg.getWindow().setWidth(800);
                    dlg.getWindow().setHeight(500);
                    dlg.show();
                }));
    }



    private Button createToolBarButton(IconType icon, final Runnable action){
        final Button btn = new Button("", ImageResources.getImageView(icon));

        btn.setOnMouseEntered(me -> btn.setEffect(new Glow(0.5)));

        btn.setOnMouseExited(me -> btn.setEffect(null));

        if(action != null){
            btn.setOnMouseClicked(me -> action.run());
        }

        btn.setStyle(Style_ToolBar_Background);

        return btn;
    }

}
