package vidada.viewsFX;

import java.awt.EventQueue;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import vidada.views.dialoges.ManageLibraryFoldersDialog;
import vidada.views.dialoges.ManageTagsDialog;
import vidada.views.dialoges.ScanAndUpdateDialog;
import vidada.views.dialoges.SettingsDialog;
import vidada.viewsFX.ImageResources.IconType;

public class VidadaToolBar extends ToolBar{

	private static final String Style_ToolBar_Background = "-fx-background-color: #505050;";

	public VidadaToolBar(){

		this.setStyle(Style_ToolBar_Background);



		getItems().add(
				createToolBarButton(IconType.FOLDER_ICON_32, new Runnable() {

					@Override
					public void run() {

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								ManageLibraryFoldersDialog libDialog = new ManageLibraryFoldersDialog(null);
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

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								ScanAndUpdateDialog scanUpdateDialog = new ScanAndUpdateDialog(null);
								scanUpdateDialog.setLocationRelativeTo(null);
								scanUpdateDialog.setVisible(true);
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
