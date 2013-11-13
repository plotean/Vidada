package vlcj.fx;

public interface IMediaPlayerBehavior {

	/**
	 * Activate this behavior for the given media player
	 * @param mediaPlayer
	 */
	public abstract void activate(MediaPlayerVLC mediaPlayer);

	/**
	 * Deactivate this behaviour for the given media player
	 * @param mediaPlayer
	 */
	public abstract void remove(MediaPlayerVLC mediaPlayer);

}