package vidada.viewsFX.controls;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;

import org.controlsfx.control.AutoCompletionBinding;
import org.controlsfx.control.TextFields;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class TagItControlSkin<T> extends BehaviorSkinBase<TagItControl<T>, BehaviorBase<TagItControl<T>>> {

	private final FlowPane tagsView = new FlowPane();
	private final Insets tagMargrin = new Insets(5,5,0,0);

	private AutoCompletionBinding<T> autoCompletionBinding;
	private TextField tagEdit = null;


	public TagItControlSkin(TagItControl<T> control) {
		super(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));

		getChildren().add(tagsView);
		control.getTags().addListener(tagsChangedListener);
		registerChangeListener(control.suggestionProviderProperty(), "SUGGESTION_PROVIDER");


		layoutTags();
	}

	/***************************************************************************
	 *                                                                         *
	 * Overriding Public API                                                   *
	 *                                                                         *
	 **************************************************************************/


	@Override protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);

		if ("SUGGESTION_PROVIDER".equals(p)) {
			updateSuggestionProvider();
		}
	}


	/***************************************************************************
	 *                                                                         *
	 * Special Listeners                                                       *
	 *                                                                         *
	 **************************************************************************/

	private final ListChangeListener<T> tagsChangedListener = new ListChangeListener<T>(){
		@Override
		public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends T> changeEvent) {
			// Occurs when the Tags-Collection has been changed
			layoutTags();
		}
	};


	/***************************************************************************
	 *                                                                         *
	 * Private implementation                                                  *
	 *                                                                         *
	 **************************************************************************/

	private void layoutTags(){

		clearTags();

		ObservableList<T> tags = getSkinnable().getTags();

		List<Node> tagNodes = new ArrayList<>(tags.size());

		for (T tagModel : tags) {
			Node tview = getTagView(tagModel);
			FlowPane.setMargin(tview, tagMargrin);
			tagNodes.add(tview);
		}

		tagsView.getChildren().addAll(tagNodes);

		// Last control is the editable TextField
		// which enables the user to add tags easily
		tagsView.getChildren().add(getDynamicTagEdit());
	}

	private Node getTagView(final T tagModel){
		return getSkinnable().getTagNodeFactory().call(tagModel);
	}

	private TextField getDynamicTagEdit(){
		if(tagEdit == null){
			tagEdit = new TextField();
			tagEdit.setMinWidth(150);

			autoCompletionBinding = TextFields.bindAutoCompletion(tagEdit, getSkinnable().getSuggestionProvider());

			tagEdit.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent ke) {
					switch (ke.getCode()) {
					case SPACE:
					case ENTER:
						onAddNewTag(tagEdit.getText());
						break;
					default:
						break;
					}
				}
			});
		}
		return tagEdit;
	}

	private void updateSuggestionProvider(){
		if(getSkinnable().getSuggestionProvider() != null){
			autoCompletionBinding.setSuggestionProvider(getSkinnable().getSuggestionProvider());
		}
	}

	private void onAddNewTag(String text) {
		T newTag = null;
		if(getSkinnable().getTagModelFactory() != null){
			newTag = getSkinnable().getTagModelFactory().call(text);
			if(newTag != null)
				getSkinnable().getTags().add(newTag);
		}
	}

	private void clearTags(){
		tagsView.getChildren().clear();
	}
}
