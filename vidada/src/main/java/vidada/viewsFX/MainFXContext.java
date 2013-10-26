package vidada.viewsFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import archimedesJ.util.OSValidator;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;


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


		//AquaFx.style();

		BorderPane contentPane = new BorderPane();
		contentPane.setTop(new VidadaToolBar());
		contentPane.setCenter(createMainContent());

		StackPane root = new StackPane();
		root.getChildren().add(contentPane);
		primaryStage.setScene(new Scene(root, 300, 250));

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent we) {

				//SessionManager.getObjectContainer().close();

			}
		});
		primaryStage.show();
	}


	@Override
	public void stop(){
		System.out.println("MainFXContext.stop(): bye bye");
		Platform.exit();
		System.exit(0);
	}

	private Node createMainContent(){	

		TabPane mainTab = new TabPane();

		Tab browserTab = new Tab("Browser");
		Tab fileBrowserTab = new Tab("File Browser");
		browserTab.setClosable(false);
		fileBrowserTab.setClosable(false);
		mainTab.getTabs().add(browserTab);
		mainTab.getTabs().add(fileBrowserTab);


		MediaBrowserFX contentPane = new MediaBrowserFX();
		browserTab.setContent(contentPane);


		contentPane.setDataContext(ServiceProvider.Resolve(IMediaService.class).getAllMediaData());

		TitledPane filterPane = new TitledPane("Filter", new Button("Button"));
		contentPane.setTop(filterPane);



		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}












}
