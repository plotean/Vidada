package vidada.viewsFX.player;

import javafx.scene.layout.BorderPane;

/**
 * Represents an abstract media player
 * @author IsNull
 *
 */
public abstract class MediaPlayerFx extends BorderPane {

	/**
	 * Gets the media controller of this player
	 * @return
	 */
	public abstract IMediaController getMediaController();



	@Override
	public abstract void setWidth(double width);

	@Override
	public abstract void setHeight(double height);

}
