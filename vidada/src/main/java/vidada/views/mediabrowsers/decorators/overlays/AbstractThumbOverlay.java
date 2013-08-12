package vidada.views.mediabrowsers.decorators.overlays;

import java.awt.Rectangle;

import javax.swing.JComponent;

import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;
import archimedesJ.swing.components.thumbpresenter.JThumbInteractionController;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

public abstract class AbstractThumbOverlay implements IThumbOverlayComponent {

	protected JThumbOverlayDecorator thumbOverlayManager;
	protected JThumbInteractionController selectionService;
	protected JThumbViewPortRenderer viewPortRenderer;

	protected AbstractThumbOverlay() {

	}

	@Override
	public void onOverlayRegistered(JThumbOverlayDecorator thumbOverlayDecorator) {
		this.thumbOverlayManager = thumbOverlayDecorator;
		this.viewPortRenderer = thumbOverlayManager.getJThumbViewer();
		this.selectionService = viewPortRenderer.getInteractionController();
	}

	@Override
	public abstract JComponent getOverlay();

	/**
	 * Update the overlay component position and size
	 */
	protected void updateThumbOverlayLocation() {

		IBaseThumb thumbitem = selectionService.getFirstSelected();

		if (thumbitem != null) {
			Rectangle bounds = viewPortRenderer.getItemBounds(thumbitem);
			if (bounds != null) {
				JComponent overlayComponent = getOverlay();
				overlayComponent.setBounds((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
				System.out.println("updateThumbOverlayLocation: overlayComponent bounds updated to: " + overlayComponent.getBounds());
			} else {
				System.err.println("updateThumbOverlayLocation: ItemBounds for " + thumbitem + "  could not be determined (NULL)");
			}
		} else {
			System.err.println("updateThumbOverlayLocation: No thumb selected! selectionService.getFirstSelected()");
		}
	}

}
