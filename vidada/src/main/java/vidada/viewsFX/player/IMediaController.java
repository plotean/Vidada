package vidada.viewsFX.player;

/**
 * Represents the ability to control the playback of a media
 * @author IsNull
 *
 */
public interface IMediaController {

	/**
	 * 
	 * @param aspectRatio
	 */
	void setCropGeometry(String aspectRatio);

	/**
	 * 
	 * @param factor
	 */
	void setScale(float factor);


	/**
	 * Play the given file
	 * @param file
	 */
	void playMedia(String file);

	/**
	 * Refresh the current player.
	 * 
	 */
	void refresh();

	/**
	 * Stop the pla- back, and sets position to zero
	 */
	void stop();

	/**
	 * Pause or Unpause the current play-back
	 */
	void togglePause();

	/**
	 * Set the player position to the given relative
	 * 
	 * @param pos
	 */
	void setPosition(float relativePos);

	/**
	 * Get the players current relative position
	 * 
	 * @return
	 */
	float getPosition();
}
