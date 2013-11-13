package vlcj;

import uk.co.caprica.vlcj.player.MediaPlayer;
import vidada.viewsFX.player.IMediaController;

/**
 * Implements a dynamic media controller.
 * This means that the MediaPlayer can be changed at any time
 * 
 * @author IsNull
 *
 */
public class VLCMediaController implements IMediaController {

	private MediaPlayer _mediaPlayer;
	private String lastPlayedMedia = null;

	public void bind(MediaPlayer mediaPlayerVLC){
		_mediaPlayer = mediaPlayerVLC;
	}

	public void unbind(MediaPlayer vlcMediaPlayer) {
		if(_mediaPlayer == vlcMediaPlayer)
			_mediaPlayer = null;
	}

	@Override
	public void refresh() {
		if(lastPlayedMedia != null)
		{
			float lastPos = this.getPosition();
			playMedia(lastPlayedMedia);
			this.setPosition(lastPos);
		}
	}


	@Override
	public void playMedia(String file) {
		lastPlayedMedia = file;
		// vlc(j) path fix
		file = file.replace("file:/", "file:///");

		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			boolean success = mediaPlayer.playMedia(file);
			System.out.println("playMedia succeeded? " + success + " - playing file: " + file);
		}else{
			System.err.println("media player not avaiable!");
		}
	}

	@Override
	public void setCropGeometry(String aspectRatio) {
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.setCropGeometry(aspectRatio);
		}else {
			System.err.println("media player not avaiable!");
		}
	}

	@Override
	public  void setScale(float factor){
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.setScale(factor);
			System.out.println("scale set to " + factor + " scale is now " + mediaPlayer.getScale());
		}else {
			System.err.println("media player not avaiable!");
		}
	}

	@Override
	public float getPosition() {
		float pos = 0;
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			pos = mediaPlayer.getPosition();
		}
		return pos;
	}

	@Override
	public void setPosition(float pos) {
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.setPosition(pos);
		}else{
			System.err.println("media player not avaiable!");
		}
	}


	@Override
	public void stop() {
		lastPlayedMedia = null;
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.stop();
		}
	}

	@Override
	public void togglePause(){
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}else{
			System.err.println("media player not avaiable!");
		}
	}








}
