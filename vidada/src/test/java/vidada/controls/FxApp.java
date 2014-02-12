package vidada.controls;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import vidada.viewsFX.controls.TagControl;
import vidada.viewsFX.controls.TagControl.RemovedActionEvent;
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

		BorderPane root = new BorderPane();

		final TagItPanel<String> tagit = new TagItPanel<>();
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

		tagit.setTagNodeFactory(new Callback<String, Node>() {
			@Override
			public Node call(final String name) {
				TagControl tag = new TagControl(name);
				tag.setRemovable(true);
				tag.setOnRemoveAction(new EventHandler<TagControl.RemovedActionEvent>() {
					@Override
					public void handle(RemovedActionEvent arg0) {
						tagit.getTags().remove(name);
					}
				});
				return tag;
			}
		});


		Button btnAutoComplete = new Button("Enable auto complete");
		btnAutoComplete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				SuggestionProvider<String> tagSuggestionProvider = SuggestionProvider.create("Hell","Hello","Hello World", "App", "Apple", "Test");
				tagit.setSuggestionProvider(tagSuggestionProvider);
			}
		});

		root.setBottom(btnAutoComplete);
		root.setCenter(tagit);

		BorderPane.setMargin(root, new Insets(15));
		return root;
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
