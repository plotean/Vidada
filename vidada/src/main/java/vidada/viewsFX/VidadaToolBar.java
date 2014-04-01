package vidada.viewsFX;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.viewsFX.ImageResources.IconType;
import vidada.viewsFX.dialoges.SettingsDialog;
import vidada.viewsFX.dialoges.SimpleDialog;
import vidada.viewsFX.dialoges.SynchronizeDialog;
import vidada.viewsFX.libraries.LibraryManagerController;

public class VidadaToolBar extends ToolBar{

    private static final String Style_ToolBar_Background = "-fx-background-color: #505050;";

    public VidadaToolBar(){

        this.setStyle(Style_ToolBar_Background);

        getItems().add(
                createToolBarButton(IconType.FOLDER_ICON_32, () -> {

                    Node libManager = FXMLLoaderX.load("libraries/LibraryManagerView.fxml");

                    SimpleDialog libraryDialog = new SimpleDialog(null, "Vidada Library Manager", libManager);
                    libraryDialog.setMasthead("Here you can add or remove media libraries.");
                    libraryDialog.show();

                    LibraryManagerController vm  = (LibraryManagerController)libManager.getUserData();

                    if(vm != null && vm.hasChanges()) {
                        Action response = Dialogs.create()
                                .title("Vidada - Libraries have changed")
                                .masthead("Do you want to synchronize your media library?")
                                .message("The media libraries have changed which means that your current media database needs to be updated. Do you want to update now?")
                                .showConfirm();

                        if (response == Dialog.Actions.YES) {
                            UpdateMediaLibraryAction updateMediaLibraryAction = new UpdateMediaLibraryAction(null);
                            updateMediaLibraryAction.actionPerformed(null);
                        }
                    }

                }));

        getItems().add(
                createToolBarButton(IconType.UPDATELIB_ICON_32, () -> Platform.runLater(() -> {
                    SynchronizeDialog scanUpdateDialog = new SynchronizeDialog(null);
                    scanUpdateDialog.show();
                })));

        getItems().add(
                createToolBarButton(IconType.TAG_ICON_32, () -> {

                    System.out.println("Tag manager not implemented!");
                }));

        getItems().add(new Separator());

        getItems().add(
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
