package vlcj.fx;

import vidada.viewsFX.player.MediaPlayerFx;

public interface IMediaPlayerBehavior {

	/**
	 * Activate this behavior for the given media player
	 * @param mediaPlayer
	 */
	public abstract void activate(MediaPlayerFx mediaPlayer);

	/**
	 * Deactivate this behaviour for the given media player
	 * @param mediaPlayer
	 */
	public abstract void disable(MediaPlayerFx mediaPlayer);

}