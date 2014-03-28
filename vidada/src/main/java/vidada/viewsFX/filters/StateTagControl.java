package vidada.viewsFX.filters;

import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventListenerEx;
import javafx.scene.input.MouseEvent;
import vidada.client.viewmodel.tags.TagViewModel;
import vidada.viewsFX.controls.TagControl;

/**
 * A enhanced TagControl which supports States.
 * The TagViewModel can have different states which are visually represented.
 */
public class StateTagControl extends TagControl<TagViewModel> {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    /**
     * Occurs when the State in a TagViewModel has been changed.
     */
    private final EventListenerEx<EventArgsG<TagViewModel>> stateListener = (s, e) -> updateStateToView(e.getValue());


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new StateTagControl
     * @param model
     */
    public StateTagControl(TagViewModel model){
        super(model);

        tagProperty().addListener((observableValue, oldValue, newValue) -> {
            if(oldValue != null) removeStateListener(oldValue);
            if(newValue != null) addStateListener(newValue);
        });

        setOnMouseClicked((MouseEvent me) -> {
            if(me.getClickCount() == 1){
                getTag().toggleState();
            }
        });

        addStateListener(model);
    }


    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/

    private void addStateListener(TagViewModel model){
        model.getTagStateChangedEvent().add(stateListener);
        updateStateToView(model);
    }

    private void removeStateListener(TagViewModel model){
        model.getTagStateChangedEvent().remove(stateListener);
    }

    private void updateStateToView(TagViewModel vm){
        // We want to update the Tags visual appearance to reflect the new state.
        this.getStyleClass().clear();
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);

        switch (vm.getState()) {
            case Allowed:
            case Required:
            case Blocked:
            case Indeterminate:
            case Unavaiable:
                String styleName = vm.getState().toString().toLowerCase();
                // The default styles correspond with the enum names.
                this.getStyleClass().add(styleName);
                break;
        }
    }


}
