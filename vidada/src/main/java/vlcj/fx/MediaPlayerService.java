package vlcj.fx;

import javafx.scene.Node;
import vidada.viewsFX.player.IMediaController;
import vidada.viewsFX.player.IMediaPlayerService;
import vlcj.VlcjUtil;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

public class MediaPlayerService implements IMediaPlayerService {

	private MediaPlayerComponent mediaPlayerComponent;


	/**
	 * Abstract representation of a mediaplayer
	 * @author IsNull
	 *
	 */
	static class MediaPlayerComponent implements IMediaPlayerComponent {

		private EventHandlerEx<EventArgs> requestReleaseEvent = new EventHandlerEx<EventArgs>();

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
		public Node getSharedPlayer() { return player; }

		/**
		 * Represents the mediaplayer controller
		 */
		@Override
		public IMediaController getMediaController(){ return mediaController;}


		private Node player;
		private IMediaController mediaController;

		public MediaPlayerComponent(Node player, IMediaController mediaController ){
			this.player = player;
			this.mediaController = mediaController;
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
			mediaPlayerComponent = new MediaPlayerComponent(vlcPlayerFX, vlcPlayerFX.getMediaController());
		}else{
			mediaPlayerComponent.freePlayer();
		}
		return mediaPlayerComponent;
	}


	@Override
	public boolean isMediaPlayerAvaiable() {
		return VlcjUtil.isVlcjAvaiable();
	}
}
