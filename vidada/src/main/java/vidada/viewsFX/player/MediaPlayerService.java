package vidada.viewsFX.player;

import vlcj.VLCjUtil;
import vlcj.fx.MediaPlayerVLC;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;

public class MediaPlayerService implements IMediaPlayerService {

	private MediaPlayerComponent mediaPlayerComponent;


	/**
	 * Abstract representation of a mediaplayer
	 * @author IsNull
	 *
	 */
	static class MediaPlayerComponent implements IMediaPlayerComponent {

		private EventHandlerEx<EventArgs> requestReleaseEvent = new EventHandlerEx<>();

		/**
		 * Raised when this shared MediaPlayer component should be released by its current user
		 * @return
		 */
		@Override
		public IEvent<EventArgs> getRequestReleaseEvent() { return requestReleaseEvent; }

		/**
		 * Represents an mediaplayer visual
		 */
		@Override
		public MediaPlayerFx getSharedPlayer() { return player; }

		private MediaPlayerFx player;


		public MediaPlayerComponent(MediaPlayerFx player ){
			this.player = player;
		}

		/**
		 * Causes an event which requests that the player is freed from any usage
		 * (Removed from any visual tree)
		 */
		protected void freePlayer() {
			requestReleaseEvent.fireEvent(this, EventArgs.Empty);
		}
	}


	@Override
	public MediaPlayerComponent resolveMediaPlayer() {
		if(mediaPlayerComponent == null){
			MediaPlayerVLC vlcPlayerFX = new MediaPlayerVLC();
			mediaPlayerComponent = new MediaPlayerComponent(vlcPlayerFX);
		}else{
			mediaPlayerComponent.freePlayer();
		}
		return mediaPlayerComponent;
	}


	@Override
	public boolean isMediaPlayerAvaiable() {
		return VLCjUtil.isVlcjAvaiable();
	}
}
