package vidada.viewsFX;

import java.awt.EventQueue;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vidada.views.dialoges.ManageLibraryFoldersDialog;
import vidada.viewsFX.ImageResources.IconType;
import archimedesJ.util.OSValidator;


public class MainFXContext extends Application {


	public static void main(String[] args) {
		OSValidator.setForceHDPI(true);
		launch(args);
	}

	public void showMainUI(){
		MainFXContext.launch();
	}


	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Vidada 2014");

		BorderPane contentPane = new BorderPane();
		contentPane.setTop(createToolBar());
		contentPane.setCenter(createMainContent());

		StackPane root = new StackPane();
		root.getChildren().add(contentPane);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}

	private Node createMainContent(){	

		BorderPane contentPane = new BorderPane();

		return contentPane;
	}


	private static final String Style_ToolBar_Background = "-fx-background-color: #505050;";


	private ToolBar createToolBar(){

		ToolBar toolBar = new ToolBar(
				createToolBarButton(IconType.FOLDER_ICON_32, new Runnable() {

					@Override
					public void run() {
						//JFrame parentFrame = (JFrame)getTopLevelAncestor();

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {

								ManageLibraryFoldersDialog libDialog = new ManageLibraryFoldersDialog(null);
								libDialog.setLocationRelativeTo(null);
								libDialog.setVisible(true);
							}
						});
					}
				}),
				createToolBarButton(IconType.UPDATELIB_ICON_32,null),
				createToolBarButton(IconType.TAG_ICON_32,null),
				new Separator(),
				createToolBarButton(IconType.SETTINGS_ICON_32,null)

				);

		toolBar.setStyle(Style_ToolBar_Background);

		return toolBar;
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
