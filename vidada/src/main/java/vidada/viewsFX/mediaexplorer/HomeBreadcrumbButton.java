package vidada.viewsFX.mediaexplorer;

import impl.org.controlsfx.skin.BreadCrumbBarSkin.BreadCrumbButton;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

public class HomeBreadcrumbButton extends BreadCrumbButton {

	private final Popup popover = new Popup();

	public HomeBreadcrumbButton(String text, Node gfx) {
		super(text, gfx);

		popover.getContent().add(new Label("Hello World"));
		this.setOnMouseClicked(listener);
	}

	private final EventHandler<MouseEvent> listener = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				Node owner = HomeBreadcrumbButton.this;
				javafx.geometry.Point2D point = owner.localToScreen(owner.getLayoutBounds().getMaxX(),owner.getLayoutBounds().getMaxY() ); // get coordinates of node
				popover.show(owner, point.getX(), point.getY());

			}
		}
	};


}
