package vidada.viewsFX;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;

/**
 *
 */
public class SplashScreen extends BorderPane {

    private BorderPane splashLayout;
    private ProgressIndicator loadProgress;
    private Label progressText;


    private Stage initStage;


    public SplashScreen(){

        Image image = loadImage("/images/vidada.png");

        ImageView splash = new ImageView(image);

        splash.setFitHeight(180);
        splash.setSmooth(true);
        splash.setPreserveRatio(true);


        loadProgress = new ProgressIndicator();
        loadProgress.setStyle(" -fx-progress-color: orange;");
        loadProgress.setMaxWidth(60);

        progressText = new Label("Loading . . .");
        progressText.setTextFill(Color.ORANGE);
        splashLayout = new BorderPane();
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-width:2; -fx-border-color: orange;");
        splashLayout.setEffect(new DropShadow());

        splashLayout.setLeft(splash);
        splashLayout.setRight(loadProgress);
        BorderPane.setAlignment(loadProgress, Pos.CENTER);
        BorderPane.setMargin(loadProgress, new Insets(0,40,0,0));
        splashLayout.setBottom(progressText);

        progressText.setAlignment(Pos.CENTER);

    }

    public void show(Stage initStage){
        this.initStage = initStage;
        Scene splashScene = new Scene(splashLayout);
        initStage.initStyle(StageStyle.UNDECORATED);
        initStage.setScene(splashScene);
        initStage.centerOnScreen();
        initStage.show();
    }

    public void hide(){
        loadProgress.progressProperty().unbind();
        loadProgress.setProgress(1);
        FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
        fadeSplash.setFromValue(1.0);
        fadeSplash.setToValue(0.0);
        fadeSplash.setOnFinished(actionEvent -> initStage.hide());
        fadeSplash.play();
    }

    private Image loadImage(String imagePath){
        InputStream inputStream = this.getClass().getResourceAsStream(imagePath);
        return new Image(inputStream);
    }


}
