package vidada.viewsFX.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

/**
 * Represents a single tag.
 * 
 * Optionally, this control can show a remove button to remove this tag
 * using the {@link #setRemovable} method.
 *
 * @param <T> Type of Tag-Model
 * @author IsNull
 *
 */
public class TagControl<T> extends Control {

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
		public static final EventType<RemovedActionEvent> REMOVE_ACTION = new EventType<>("REMOVE_ACTION");

		public RemovedActionEvent() {
			super(REMOVE_ACTION);
		}
	}


	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final ObjectProperty<T> tag =  new SimpleObjectProperty<>(this, "tag");

	private final ObjectProperty<Boolean> removable =  new SimpleObjectProperty<>(this, "removable");

    private final ObjectProperty<Callback<T, String>> converter =  new SimpleObjectProperty<>(this, "converter");

    private final Callback<T, String> defaultConverter = x -> x.toString();


	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates an empty TagControl
	 */
	public TagControl(){
		this(null);
	}

	/**
	 * Creates a new TagControl with the given name
	 * @param tag The Tag Model to be displayed.
	 */
	public TagControl(T tag){
		setTag(tag);

        // Set default properties
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		removable.set(false);
        converter.set(defaultConverter);
	}


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Event which is fired when the remove button was clicked.
	 * @param actionListener
	 */
	public void setOnAction(EventHandler<ActionEvent> actionListener){
		addEventHandler(ActionEvent.ACTION, actionListener);
	}

	/**
	 * Event which is fired when the remove button was clicked.
	 * @param actionListener
	 */
	public void setOnRemoveAction(EventHandler<RemovedActionEvent> actionListener){
		addEventHandler(RemovedActionEvent.REMOVE_ACTION, actionListener);
	}

    /**
     * Returns the tag text derived from the Tag-Model using the {#getConverter}.
     */
    public String getText(){
        return getConverter().call(getTag());
    }


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

    /**
     * Gets the tag property
     * @return
     */
	public ObjectProperty<T> tagProperty() {
		return tag;
	}

	/**
	 * Returns the Tag-Text
	 * @return
	 */
	public T getTag() {
		return tag.get();
	}

	/**
	 * Set the Tag-Text
	 * @param tag
	 */
	public void setTag(T tag) {
		this.tag.set(tag);
	}


	public ObjectProperty<Boolean> removableProperty(){
		return removable;
	}

	/**
	 * Is this tag removable?
	 * @return
	 */
	public boolean isRemovable(){
		return removable.get();
	}

	/**
	 * Set the tag removable / disable removeable.
     * This will alter the appearance and add/remove a remove-button.
	 * @param enabled
	 */
	public void setRemovable(boolean enabled){
		removable.set(enabled);
	}


    public ObjectProperty<Callback<T, String>> getConverterProperty() {
        return converter;
    }

    /**
     * Get the String-Converter which is used to turn the tag model into a string
     * @return
     */
    public Callback<T, String> getConverter() {
        return converter.getValue();
    }

    /**
     * Set the StringConverter to convert the tag model into a string
     * By default, toString is used.
     * @param converter The new converter. Null is not allowed and will result in falling back to the default.
     */
    public void setConverter(Callback<T, String> converter) {
        if(converter != null){
            this.converter.setValue(converter);
        }else{
            this.converter.setValue(defaultConverter);
        }
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
