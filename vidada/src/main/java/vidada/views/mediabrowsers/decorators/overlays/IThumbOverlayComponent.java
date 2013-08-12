package vidada.views.mediabrowsers.decorators.overlays;

import javax.swing.JComponent;

import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;

/**
 * Represents a single overlay component which can be added to a <code>JThumbOverlayDecorator</code>
 * 
 * @author IsNull
 *
 */
public interface IThumbOverlayComponent {

	/**
	 * Occurs when this component has been registered on a <code>JThumbOverlayDecorator</code>
	 * @param thumbOverlayManager
	 */
	void onOverlayRegistered(JThumbOverlayDecorator thumbOverlayDecorator);

	/**
	 * Get the actual Component to be rendered.
	 * This method is called imediatly after the <code>IThumbOverlayComponent</code>
	 * has been registered.
	 * @return
	 */
	JComponent getOverlay();


}
