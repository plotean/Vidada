package vidada.views.directplay;

import java.awt.Graphics2D;

/**
 * 
 * @author IsNull
 *
 */
public interface IDecoupledRenderer {
	/**
	 * Called when painting is requested
	 * @param g
	 */
	public abstract void render(Graphics2D g);
}
