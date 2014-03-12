package vidada.viewsFX.player;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.layout.BorderPane;

/**
 * Represents an abstract media player
 * @author IsNull
 *
 */
public abstract class MediaPlayerFx extends BorderPane {


	/**
	 * DPI resolution multiplier. 
	 * Default = 1.0
	 * Retina = 2.0
	 */
	private float dpiMultiplier = 2.0f;


	/**
	 * Gets the media controller of this player
	 * @return
	 */
	public abstract IMediaController getMediaController();



	@Override
	public abstract void setWidth(double width);

	@Override
	public abstract void setHeight(double height);

	public abstract double getRealWidth();

	public abstract double getRealHeight();

	public float getDpiMultiplier() {
		return dpiMultiplier;
	}

	public void setDpiMultiplier(float dpiMultiplier) {
		this.dpiMultiplier = dpiMultiplier;
	}

	private Set<IMediaPlayerBehavior> behaviors = new HashSet<>();

	public void addBehavior(IMediaPlayerBehavior behavior){
		if(behaviors.add(behavior)){
			behavior.activate(this);
		}
	}

	public void removeBehavior(IMediaPlayerBehavior behavior){
		if(behaviors.remove(behavior))
			behavior.disable(this);
	}


}
