package vlcj.fx;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import vidada.viewsFX.player.MediaPlayerFx;

public class MediaPlayerSeekBehaviour implements IMediaPlayerBehavior {

	transient public static final IMediaPlayerBehavior Instance = new MediaPlayerSeekBehaviour();


	private MediaPlayerSeekBehaviour(){
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
}
