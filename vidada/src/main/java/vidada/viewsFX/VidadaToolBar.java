package vidada.viewsFX;

import java.awt.EventQueue;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.swing.Action;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import vidada.commands.UpdateMediaLibraryAction;
import vidada.views.dialoges.ManageLibraryFoldersDialog;
import vidada.views.dialoges.ManageTagsDialog;
import vidada.views.dialoges.SettingsDialog;
import vidada.viewsFX.ImageResources.IconType;
import vidada.viewsFX.dialoges.SynchronizeDialog;

public class VidadaToolBar extends ToolBar{

	private static final String Style_ToolBar_Background = "-fx-background-color: #505050;";

	public VidadaToolBar(){

		this.setStyle(Style_ToolBar_Background);



		getItems().add(
				createToolBarButton(IconType.FOLDER_ICON_32, new Runnable() {

					@Override
					public void run() {
						final Callback<Void,Void> cb = new Callback<Void,Void>(){
							@Override
							public Void call(Void arg0) {

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										org.controlsfx.control.action.Action response = Dialogs.create()
												.title("Vidada - Libraries have changed")
												.masthead("Do you want to synchronize your media library?")
												.message("The media libraries have changed which means that your current media database needs to be updated. Do you want to update now?")
												.showConfirm();

										if(response == Dialog.Actions.YES){
											Action updateMediaLibraryAction = new UpdateMediaLibraryAction(null); 
											updateMediaLibraryAction.actionPerformed(null);
										}
									}
								});
								return null;
							}
						};

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// TODO Replace with JavaFX dialog
								ManageLibraryFoldersDialog libDialog = new ManageLibraryFoldersDialog(null, cb);
								libDialog.setLocationRelativeTo(null);
								libDialog.setVisible(true);
							}
						});

					}
				}));

		getItems().add(
				createToolBarButton(IconType.UPDATELIB_ICON_32, new Runnable() {

					@Override
					public void run() {

						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								SynchronizeDialog scanUpdateDialog = new SynchronizeDialog(null);
								scanUpdateDialog.show();
							}
						});


					}
				}));

		getItems().add(
				createToolBarButton(IconType.TAG_ICON_32, new Runnable() {

					@Override
					public void run() {

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								ManageTagsDialog manageTagsDialog = new ManageTagsDialog();
								manageTagsDialog.setLocationRelativeTo(null);
								manageTagsDialog.setVisible(true);
							}
						});
					}
				}));

		getItems().add(new Separator());

		getItems().add(
				createToolBarButton(IconType.SETTINGS_ICON_32, new Runnable() {

					@Override
					public void run() {

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								SettingsDialog settingsDialog = new SettingsDialog(null);
								settingsDialog.setLocationRelativeTo(null);
								settingsDialog.setVisible(true);
							}
						});
					}
				}));


	}



	private Button createToolBarButton(IconType icon, final Runnable action){
		final Button btn = new Button("", ImageResources.getImageView(icon));

		btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				btn.setEffect(new Glow(0.5));
			}	
		});

		btn.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				btn.setEffect(null);
			}	
		});

		if(action != null){
			btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent me) {
					action.run();	
				}
			});
		}

		btn.setStyle(Style_ToolBar_Background);


		return btn;
	}

}
