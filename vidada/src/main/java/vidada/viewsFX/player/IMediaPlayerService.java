package vidada.viewsFX.player;

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
		MediaPlayerFx getSharedPlayer();

	}

	/**
	 * Gets a (shared) media player
	 * @return
	 */
	IMediaPlayerComponent resolveMediaPlayer();

	/**
	 * Is a media player available?
	 * @return
	 */
	boolean isMediaPlayerAvaiable();

}
