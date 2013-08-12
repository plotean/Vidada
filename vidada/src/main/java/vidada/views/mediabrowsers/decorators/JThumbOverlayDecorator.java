package vidada.views.mediabrowsers.decorators;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Scrollable;

import vidada.views.mediabrowsers.decorators.overlays.IThumbOverlayComponent;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;

/**
 * JThumbOverlayDecorator
 * 
 * Manages the JThumbViewer Overlays
 * @author IsNull
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class JThumbOverlayDecorator  extends JLayeredPane implements Scrollable{

	protected final JThumbViewPortRenderer viewPortRenderer;
	protected final JComponent renderComponent;

	public JThumbOverlayDecorator(JThumbViewPortRenderer renderer){
		this(renderer, renderer);
	}

	public JThumbOverlayDecorator(JComponent component, JThumbViewPortRenderer renderer){
		this.viewPortRenderer = renderer;
		this.renderComponent = component;
		add(component, JLayeredPane.DEFAULT_LAYER);
	}

	private List<IThumbOverlayComponent> overlays = new ArrayList<IThumbOverlayComponent>();

	/**
	 * Register the given overlayComponent on the default layer
	 * @param overlayComponent
	 */
	public void registerOverlay(IThumbOverlayComponent overlayComponent) throws NotSupportedException{
		registerOverlay(overlayComponent, JLayeredPane.DRAG_LAYER);
	}

	/**
	 * Register the given overlayComponent on the given Layer
	 * @param overlayComponent
	 * @param layer
	 */
	public void registerOverlay(IThumbOverlayComponent overlayComponent, Integer layer) throws NotSupportedException{

		if(overlayComponent != null){

			overlayComponent.onOverlayRegistered(this);
			JComponent component = overlayComponent.getOverlay();
			
			if(component != null){				
				overlays.add(overlayComponent);
				System.out.println("adding " + component + " to layer " + layer);
				
				// Add the component as a new Layer to this LayeredPane
				add(component, layer); // Integer - not int!  stupid stupid java!
			}else
				throw new NotSupportedException("The overlayComponent (IThumbOverlayComponent) must return non Null on getOverlay(). JThumbOverlayDecorator");
		}else
			throw new IllegalArgumentException("overlayComponent must not be null!");
	}


	/**
	 * 
	 * @return
	 */
	public JThumbViewPortRenderer getJThumbViewer(){
		return viewPortRenderer;
	}


	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		viewPortRenderer.setBounds(0, 0, width, height);
		renderComponent.setBounds(0, 0, width, height);
	}

	@Override
	public Dimension getSize() {
		return viewPortRenderer.getSize();
	}

	@Override
	public Dimension getPreferredSize(){
		return viewPortRenderer.getPreferredSize();
	}


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return viewPortRenderer.getPreferredScrollableViewportSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return viewPortRenderer.getScrollableUnitIncrement(visibleRect, orientation, direction);
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return viewPortRenderer.getScrollableBlockIncrement(visibleRect, orientation, direction);
	}


	@Override
	public boolean getScrollableTracksViewportWidth() {
		return viewPortRenderer.getScrollableTracksViewportWidth();
	}


	@Override
	public boolean getScrollableTracksViewportHeight() {
		return viewPortRenderer.getScrollableTracksViewportHeight();
	}

}
