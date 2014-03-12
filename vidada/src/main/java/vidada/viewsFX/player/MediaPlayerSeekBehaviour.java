package vidada.viewsFX.player;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MediaPlayerSeekBehaviour implements IMediaPlayerBehavior {


	/* (non-Javadoc)
	 * @see vlcj.fx.IMediaPlayerBehaviour#activate(vlcj.fx.MediaPlayerVLC)
	 */
	@Override
	public void activate(final MediaPlayerFx mediaPlayer){
		mediaPlayer.addEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}

	/* (non-Javadoc)
	 * @see vlcj.fx.IMediaPlayerBehaviour#remove(vlcj.fx.MediaPlayerVLC)
	 */
	@Override
	public void disable(final MediaPlayerFx mediaPlayer){
		mediaPlayer.removeEventHandler(MouseEvent.MOUSE_MOVED, eventListener);
	}



	private EventHandler<MouseEvent> eventListener = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent me) {

			MediaPlayerFx mediaPlayer = (MediaPlayerFx) me.getSource();
			double width = mediaPlayer.getRealWidth();
			double relativePos = me.getX() / width;
			System.out.println(me.getX() + " " + width);
			System.out.println("MediaPlayerSeekBehaviour: pos " + relativePos);
			mediaPlayer.getMediaController().setPosition((float)relativePos);
		}
	};


}
