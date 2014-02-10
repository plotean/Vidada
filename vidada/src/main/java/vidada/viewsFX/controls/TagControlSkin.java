package vidada.viewsFX.controls;

import java.util.Collections;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class TagControlSkin extends BehaviorSkinBase<TagControl, BehaviorBase<TagControl>>{

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/


	private final HBox layout;
	private final Label tagname;
	private final Circle tagCircle;

	public TagControlSkin(TagControl control) {
		super(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));

		layout = new HBox();

		tagCircle = new Circle();
		tagCircle.setRadius(4);
		tagCircle.setId("tag-dot");

		tagname = new Label("Cool");
		tagname.setAlignment(Pos.CENTER);

		layout.setSpacing(5);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(tagCircle, tagname);


		getChildren().add(layout);

		registerChangeListener(control.textProperty(), "TEXT");

		updateView();
	}


	/***************************************************************************
	 *                                                                         *
	 * Overriding Public API                                                   *
	 *                                                                         *
	 **************************************************************************/

	@Override protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);

		if ("TEXT".equals(p)) {
			updateView();
		}
	}

	@Override
	protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
		double childsPref = 0;
		for (Node child : layout.getChildren()) {
			childsPref += snapSize(child.prefWidth(h)) + layout.getSpacing();
		}
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
		TagControl control = getSkinnable();
		if(control != null){
			tagname.setText(control.getText());
		}else {
			tagname.setText("");
		}
	}
}
