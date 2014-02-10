package vidada.viewsFX.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Represents a single tag
 * @author IsNull
 *
 */
public class TagControl extends Control {


	public static final String STYLE_CLASS_REQUIRED = "required";
	public static final String STYLE_CLASS_BLOCKED = "blocked";



	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final ObjectProperty<String> text =  new SimpleObjectProperty<>(this, "text");




	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * 
	 */
	public TagControl(){
		this("");
	}

	/**
	 * 
	 * @param text
	 */
	public TagControl(String text){
		setText(text);
		getStyleClass().add(DEFAULT_STYLE_CLASS);
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
	public ObjectProperty<String> textProperty() {
		return text;
	}

	/**
	 * Returns the Tag-Text
	 * @return
	 */
	public String getText() {
		return text.get();
	}

	/**
	 * Set the Tag-Text
	 * @param text
	 */
	public void setText(String text) {
		this.text.set(text);
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	public static final String DEFAULT_STYLE_CLASS = "tag-control";

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TagControlSkin(this);
	}

	/** {@inheritDoc} */
	@Override protected String getUserAgentStylesheet() {
		return TagControl.class.getResource("tagcontrol.css").toExternalForm();
	}
}
