package vidada.views.mediabrowsers.decorators.overlays;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;

@SuppressWarnings("serial")
public class TestOverlay extends JPanel implements IThumbOverlayComponent{

	public TestOverlay(){
		setBackground(Color.PINK);
		setBounds(20, 20, 200, 200);
	}


	@Override
	public void onOverlayRegistered(JThumbOverlayDecorator thumbOverlayDecorator) {
		System.out.println("TestOverlay registered!");
	}

	@Override
	public JComponent getOverlay() {

		JButton bntButton = new JButton();
		bntButton.setBounds(600, 40, 200, 200);
		add(bntButton, JLayeredPane.DRAG_LAYER);
		return bntButton;
	}

}
