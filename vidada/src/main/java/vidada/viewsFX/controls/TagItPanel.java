package vidada.viewsFX.controls;

import java.util.Collection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import org.controlsfx.control.AutoCompletionBinding.ISuggestionRequest;

/**
 * A TagItPanel displays a collection of Tags.
 * 
 * It has special features such as the ability to allow the user to add / create new Tags.
 *
 * @param <T>
 */
public class TagItPanel<T> extends Control{

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final ObservableList<T> tags = FXCollections.observableArrayList();



	private final ObjectProperty<Callback<T, Node>> tagNodeFactory = new SimpleObjectProperty<>(this, "tagNodeFactory");

	private final ObjectProperty<Callback<String, T>> tagModelFactory =  new SimpleObjectProperty<>(this, "tagModelFactory");

	private final ObjectProperty<Callback<ISuggestionRequest, Collection<T>>> suggestionProvider = new SimpleObjectProperty<>(this, "suggestionProvider");


	/**
	 * Default tag node factory. This factory is used when no custom factory is specified by the user.
	 */
	private final Callback<T, Node> defaultTagNodeFactory = new Callback<T, Node>(){
		@Override
		public Node call(T tagModel) {
			return new TagControl(tagModel != null ? tagModel.toString() : "<null>");
		}
	};


	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates an new TagItPanel
	 */
	public TagItPanel(){
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		setTagNodeFactory(defaultTagNodeFactory);
	}



	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/



	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Gets the tags collection.
	 * @return
	 */
	public ObservableList<T> getTags(){ return tags; }


	//
	//---- Tag-Suggestion Provider
	//

	/**
	 * Set the Tag-Suggestion provider
	 * @param suggestionProvider
	 */
	public void setSuggestionProvider(Callback<ISuggestionRequest, Collection<T>> suggestionProvider){
		this.suggestionProvider.set(suggestionProvider);
	}

	/**
	 * Gets the current Tag-Suggestion provider
	 * @return
	 */
	public Callback<ISuggestionRequest, Collection<T>> getSuggestionProvider(){
		return suggestionProvider.get();
	}

	public ObjectProperty<Callback<ISuggestionRequest, Collection<T>>> suggestionProviderProperty(){
		return suggestionProvider;
	}


	//
	//---- Tag-Model Factory
	//

	public final ObjectProperty<Callback<String, T>> tagModelFactoryProperty() {
		return tagModelFactory;
	}

	/**
	 * Sets the tag-model factory to create Tags
	 */
	public final void setTagModelFactory(Callback<String, T> value) {
		tagModelFactoryProperty().set(value);
	}

	/**
	 * Returns the tag-model factory that is used to create tags dynamically
	 */
	public final Callback<String, T> getTagModelFactory() {
		return tagModelFactory.get();
	}

	//
	//---- Tag-Node Factory
	//

	public final ObjectProperty<Callback<T, Node>> tagNodeFactoryProperty() {
		return tagNodeFactory;
	}

	/**
	 * Sets the tag-node factory to create Nodes which represent a single tag.
	 * <code>null</code> is not allowed and will result in a fall back to the default factory.
	 */
	public final void setTagNodeFactory(Callback<T, Node> value) {
		if(value == null){
			value = defaultTagNodeFactory;
		}
		tagNodeFactoryProperty().set(value);
	}

	/**
	 * Returns the tag-node factory that will be used to create 
	 */
	public final Callback<T, Node> getTagNodeFactory() {
		return tagNodeFactory.get();
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	public static final String DEFAULT_STYLE_CLASS = "tagit-panel";

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TagItPanelSkin<T>(this);
	}

	/** {@inheritDoc} */
	@Override protected String getUserAgentStylesheet() {
		return TagControl.class.getResource("tagitpanel.css").toExternalForm();
	}
}
