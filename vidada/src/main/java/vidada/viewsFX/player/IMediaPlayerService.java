package vidada.viewsFX.player;

import javafx.scene.Node;
import archimedesJ.events.EventArgs;
import archimedesJ.events.IEvent;

public interface IMediaPlayerService {

	/**
	 * Abstract representation of a media player
	 * @author IsNull
	 *
	 */
	static interface IMediaPlayerComponent {
		/**
		 * Raised when this shared MediaPlayer component should be released by its current user
		 * @return
		 */
		IEvent<EventArgs> getRequestReleaseEvent();

		/**
		 * Represents an shared media player visual Node
		 */
		Node getSharedPlayer();

		/**
		 * Represents the media player controller
		 */
		IMediaController getMediaController();

	}

	/**
	 * Gets a (shared) media player
	 * @return
	 */
	IMediaPlayerComponent resolveMediaPlayer();

	/**
	 * Is a mediaplayer avaiable
	 * @return
	 */
	boolean isMediaPlayerAvaiable();

}
