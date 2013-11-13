package vlcj.fx;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MediaPlayerSeekBehaviour implements IMediaPlayerBehavior {

	public MediaPlayerSeekBehaviour(){
	}

	private EventHandler<MouseEvent> eventListener = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent me) {

			MediaPlayerVLC mediaPlayer = (MediaPlayerVLC) me.getSource();
			double relativePos = me.getX() / mediaPlayer.getWidth();
			mediaPlayer.getMediaController().setPosition((float)relativePos);
		}
	};

	/* (non-Javadoc)
	 * @see vlcj.fx.IMediaPlayerBehaviour#activate(vlcj.fx.MediaPlayerVLC)
	 */
	@Override
	public void activate(final MediaPlayerVLC mediaPlayer){
		mediaPlayer.addEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}

	/* (non-Javadoc)
	 * @see vlcj.fx.IMediaPlayerBehaviour#remove(vlcj.fx.MediaPlayerVLC)
	 */
	@Override
	public void remove(final MediaPlayerVLC mediaPlayer){
		mediaPlayer.removeEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}
}
