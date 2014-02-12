package vidada.viewsFX.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Represents a single tag.
 * 
 * Optionally, this control can show a remove button to remove this tag
 * using the {@link setRemovable} method.
 * 
 * @author IsNull
 *
 */
public class TagControl extends Control {


	/***************************************************************************
	 *                                                                         *
	 * Events                                                                  *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Event which is fired when the remove button was pressed by the user
	 *
	 */
	@SuppressWarnings("serial")
	public static class RemovedActionEvent extends Event {
		public static final EventType<RemovedActionEvent> REMOVE_ACTION = new EventType<RemovedActionEvent>("REMOVE_ACTION");

		public RemovedActionEvent() {
			super(REMOVE_ACTION);
		}
	}



	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final ObjectProperty<String> text =  new SimpleObjectProperty<>(this, "text");

	private final ObjectProperty<Boolean> removable =  new SimpleObjectProperty<>(this, "removable");




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
		removable.set(false);
	}


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Event which is fired when the remove button was clicked.
	 * @param ae
	 */
	public void setOnAction(EventHandler<ActionEvent> actionListener){
		addEventHandler(ActionEvent.ACTION, actionListener);
	}

	/**
	 * Event which is fired when the remove button was clicked.
	 * @param ae
	 */
	public void setOnRemoveAction(EventHandler<RemovedActionEvent> removeListener){
		addEventHandler(RemovedActionEvent.REMOVE_ACTION, removeListener);
	}



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


	public ObjectProperty<Boolean> removableProperty(){
		return removable;
	}

	/**
	 * Is this tag removable
	 * @return
	 */
	public boolean isRemovable(){
		return removable.get();
	}

	/**
	 * Set the tag removable / disable removeable
	 * @param enabled
	 */
	public void setRemovable(boolean enabled){
		removable.set(enabled);
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	public static final String STYLE_CLASS_REQUIRED = "required";
	public static final String STYLE_CLASS_BLOCKED = "blocked";


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
