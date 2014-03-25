package vidada.viewsFX.controls;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import vidada.viewsFX.controls.TagControl.RemovedActionEvent;

import java.util.Collections;

public class TagControlSkin<T> extends BehaviorSkinBase<TagControl<T>, BehaviorBase<TagControl<T>>>{

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/


	private final HBox layout;
	private final Label tagname;
	private final Circle tagCircle;
	private final Region tagRemoveButton = new Region();

	private double spaceing = 0;

	public TagControlSkin(final TagControl control) {
		super(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));

		layout = new HBox();

		tagCircle = new Circle();
		tagCircle.setRadius(4);
		tagCircle.setId("tag-dot");


		tagname = new Label("Cool");
		tagname.setAlignment(Pos.CENTER);

		tagRemoveButton.getStyleClass().add("remove-button");

		//layout.setSpacing(5);
		layout.setAlignment(Pos.CENTER);
		HBox.setMargin(tagCircle, new Insets(0,2,0,3)); spaceing += 5;
		layout.getChildren().addAll(tagCircle, tagname);

		getChildren().add(layout);

		// register listeners

		registerChangeListener(control.tagProperty(), "TAG");
		registerChangeListener(control.removableProperty(), "REMOVABLE");

		tagRemoveButton.setOnMouseClicked(me -> {
            control.fireEvent(new RemovedActionEvent());
            me.consume();
        });

		layout.setOnMouseClicked(me -> control.fireEvent(new ActionEvent()));

		updateRemoveButton();
		updateView();
	}


	/***************************************************************************
	 *                                                                         *
	 * Overriding Public API                                                   *
	 *                                                                         *
	 **************************************************************************/

	@Override protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);

		if ("TAG".equals(p)) {
			updateView();
		}else if("REMOVABLE".equals(p)) {
			updateRemoveButton();
		}
	}


	@Override
	protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
		double childsPref = 0;
		for (Node child : layout.getChildren()) {
			childsPref += snapSize(child.prefWidth(h));// + layout.getSpacing();
		}
		childsPref += spaceing;
		return childsPref + leftInset + rightInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}


	/***************************************************************************
	 *                                                                         *
	 * Private implementation                                                  *
	 *                                                                         *
	 **************************************************************************/

	private void updateView(){
		TagControl<T> control = getSkinnable();
		if(control != null){
			tagname.setText(control.getText());
		}else {
			tagname.setText("");
		}
	}

	private void updateRemoveButton(){
		TagControl control = getSkinnable();
		if(control != null)
			if(control.isRemovable() && !layout.getChildren().contains(tagRemoveButton)){
				layout.getChildren().add(tagRemoveButton);
			}else{
				layout.getChildren().remove(tagRemoveButton);
			}
	}
}
