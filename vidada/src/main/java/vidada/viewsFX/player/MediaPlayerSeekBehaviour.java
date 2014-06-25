package vidada.viewsFX.player;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Provides instant seeking to the relative position 
 * of the mouse to the player boundaries.
 * 
 * @author IsNull
 *
 */
public class MediaPlayerSeekBehaviour implements IMediaPlayerBehavior {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaPlayerSeekBehaviour.class.getName());

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**{@inheritDoc}*/
	@Override
	public void activate(final MediaPlayerFx mediaPlayer){
		mediaPlayer.addEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}

	/**{@inheritDoc}*/
	@Override
	public void disable(final MediaPlayerFx mediaPlayer){
		mediaPlayer.removeEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}



	private EventHandler<MouseEvent> eventListener = me -> {

        MediaPlayerFx mediaPlayer = (MediaPlayerFx) me.getSource();
        double width = mediaPlayer.getRealWidth();
        double relativePos = me.getX() / width;
        logger.debug(me.getX() + " " + width);
        logger.debug("pos " + relativePos);
        mediaPlayer.getMediaController().setPosition((float)relativePos);
    };


}
