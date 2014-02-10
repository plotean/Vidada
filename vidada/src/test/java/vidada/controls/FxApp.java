package vidada.controls;

import java.util.Collection;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.control.AutoCompletionBinding.ISuggestionRequest;

import vidada.viewsFX.controls.TagControl;
import vidada.viewsFX.controls.TagItPanel;

public class FxApp extends  javafx.application.Application {

	@Override
	public void start(Stage primary) throws Exception {

		BorderPane root = new BorderPane();
		root.setCenter(new Label("Hello Test"));

		root.setTop(createTestTagIt());


		primary.setScene(new Scene(root, 600, 450));
		primary.show();
	}


	private Node createTestNode(){
		TagControl tag = new TagControl("test.tag");
		tag.getStyleClass().add("required");

		BorderPane.setMargin(tag, new Insets(15));
		return tag;
	}

	private Node createTestTagIt(){
		TagItPanel<String> tagit = new TagItPanel<>();
		tagit.getTags().add("action");
		tagit.getTags().add("comedy");
		tagit.getTags().add("horror");
		tagit.getTags().add("sifi");

		tagit.setTagModelFactory(new Callback<String, String>() {
			@Override
			public String call(String tagName) {

				tagName = tagName.trim().replaceAll("[^A-Za-z0-9 ]", "\\.");
				if(!tagName.isEmpty()){
					return tagName.toLowerCase();
				}else {
					return null;
				}
			}
		});

		tagit.setSuggestionProvider(new Callback<ISuggestionRequest, Collection<String>>() {
			@Override
			public Collection<String> call(ISuggestionRequest request) {
				return null;
			}
		});

		BorderPane.setMargin(tagit, new Insets(15));
		return tagit;
	}

	/**
	 * Primary entry point for this Application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
