package vidada.viewsFX.controls;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

import archimedesJ.exceptions.NotSupportedException;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * Skin for tag-it control
 * @author IsNull
 *
 * @param <T>
 */
public class TagItPanelSkin<T> extends BehaviorSkinBase<TagItPanel<T>, BehaviorBase<TagItPanel<T>>> {

	private static final String STYLE_CLASS_TAGIT_TEXTFIELD = "tagit-text-field";

	private final FlowPane layout = new FlowPane();

	private AutoCompletionBinding<T> autoCompletionBinding;
	private TextField tagEdit = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a new TagItControlSkin
	 * @param control
	 */
	public TagItPanelSkin(TagItPanel<T> control) {
		super(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));

		getChildren().add(layout);
		control.getTags().addListener(tagsChangedListener);
		registerChangeListener(control.suggestionProviderProperty(), "SUGGESTION_PROVIDER");
		registerChangeListener(control.editableProperty(), "EDITABLE");

		layout.setColumnHalignment(HPos.LEFT);
		layout.setRowValignment(VPos.CENTER);
		layout.setVgap(5);
		layout.setHgap(5);

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
		}else if ("EDITABLE".equals(p)) {
			updateEditable();
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

	private void updateEditable() {
		layoutTags();
	}

	private void layoutTags(){

		TagItPanel<T> control = getSkinnable();

		clearTags();


		ObservableList<T> tags = control.getTags();

		List<Node> tagNodes = new ArrayList<>(tags.size());

		for (T tagModel : tags) {
			Node tview = getTagView(tagModel);
			//FlowPane.setMargin(tview, tagMargrin);
			tagNodes.add(tview);
		}

		layout.getChildren().addAll(tagNodes);

		if(control.isEditable()){
			// Last control is the editable TextField
			// which enables the user to add tags easily
			layout.getChildren().add(getDynamicTagEdit());
		}
	}

	private Node getTagView(final T tagModel){
		return getSkinnable().getTagNodeFactory().call(tagModel);
	}

	private TextField getDynamicTagEdit(){
		if(tagEdit == null){
			tagEdit = new TextField();
			tagEdit.getStyleClass().add(STYLE_CLASS_TAGIT_TEXTFIELD);
			tagEdit.setPromptText("Add Tag...");
			tagEdit.setMinWidth(150);

			updateSuggestionProvider();

			tagEdit.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent ke) {
					switch (ke.getCode()) {
					case SPACE:
					case ENTER:
					case TAB:
						onAddNewTag(tagEdit.getText());
						tagEdit.setText("");
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

		if(autoCompletionBinding != null){
			// Dispose the old binding
			autoCompletionBinding.dispose();
		}

		if(getSkinnable().getSuggestionProvider() != null){
			autoCompletionBinding = TextFields.bindAutoCompletion(tagEdit, getSkinnable().getSuggestionProvider());

			autoCompletionBinding.setOnAutoCompleted(new EventHandler<AutoCompletionEvent<T>>() {
				@Override
				public void handle(AutoCompletionEvent<T> completionArgs) {
					System.out.println("OnAutoCompleted: " + completionArgs.getCompletion());
					if(completionArgs.getCompletion() != null)
						appendTag(completionArgs.getCompletion());
					tagEdit.setText("");
				}
			});
		}
	}


	private void onAddNewTag(String text) {
		T newTag = null;
		if(getSkinnable().getTagModelFactory() != null){
			newTag = getSkinnable().getTagModelFactory().call(text);
			if(newTag != null)
				appendTag(newTag);
			else {
				System.err.println("onAddNewTag: Tag-Model was null for tag name: '" + text + "'");
			}
		}else {
			throw new NotSupportedException("A tag-model-factory has to be set in order to create tags.");
		}
	}

	private void appendTag(T tagModel){
		getSkinnable().getTags().add(tagModel);
	}

	private void clearTags(){
		layout.getChildren().clear();
	}
}
