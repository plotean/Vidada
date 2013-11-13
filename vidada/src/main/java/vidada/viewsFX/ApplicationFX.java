package vidada.viewsFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import archimedesJ.util.OSValidator;


public class ApplicationFX extends Application {


	public static void main(String[] args) {
		OSValidator.setForceHDPI(true);
		launch(args);
	}

	public void showMainUI(){
		ApplicationFX.launch();
	}


	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			primaryStage.setTitle("Vidada 2014");

			//AquaFx.style();

			Node contentPane = new MainViewFx();

			StackPane root = new StackPane();
			root.getChildren().add(contentPane);
			primaryStage.setScene(new Scene(root, 600, 450));
			primaryStage.show();

		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void stop(){
		System.out.println("MainFXContext.stop(): bye bye");
		Platform.exit();
		System.exit(0);
	}


}
