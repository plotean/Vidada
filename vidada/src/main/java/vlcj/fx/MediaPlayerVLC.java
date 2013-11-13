package vlcj.fx;

import java.nio.ByteBuffer;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import vidada.model.settings.DatabaseSettings;
import vidada.viewsFX.player.IMediaController;
import vlcj.VLCMediaController;
import archimedesJ.util.Lists;

import com.sun.jna.Memory;

/**
 * Represents the VLC MediaPlayer
 * @author IsNull
 *
 */
public class MediaPlayerVLC extends Canvas 
{
	private final DatabaseSettings applicationSettings = DatabaseSettings.getSettings();

	/**
	 * 
	 */
	private final VLCMediaController mediaController = new VLCMediaController();

	/**
	 * Pixel writer to update the canvas.
	 */
	private final PixelWriter pixelWriter;

	/**
	 * Pixel format.
	 */
	private final WritablePixelFormat<ByteBuffer> pixelFormat;

	private MediaPlayerFactory vlcfactory;
	private MediaPlayer vlcMediaPlayer;


	public MediaPlayerVLC(){
		pixelWriter = this.getGraphicsContext2D().getPixelWriter();
		pixelFormat = PixelFormat.getByteBgraInstance();
		getMediaPlayer();

	}


	public IMediaController getMediaController(){
		return mediaController;
	}

	public MediaPlayer getMediaPlayer(){
		if(vlcMediaPlayer == null){
			vlcMediaPlayer = createMediaPlayer();
			mediaController.bind(vlcMediaPlayer);
		}
		return vlcMediaPlayer;
	}

	private MediaPlayer createMediaPlayer(){
		MediaPlayer mediaPlayer = null;
		MediaPlayerFactory playerFactory = getMediaPlayerFactory();
		mediaPlayer = playerFactory.newDirectMediaPlayer(formatCallback, renderCallback);
		return mediaPlayer;
	}

	private MediaPlayerFactory getMediaPlayerFactory(){

		if(vlcfactory == null){
			vlcfactory = createFactory();
			releasePlayer();
		}

		return vlcfactory;

	}

	private static String[] args = {"--no-plugins-cache",  "--no-video-title-show", "--no-snapshot-preview", "--quiet", "--quiet-synchro", "--intf", "dummy"};
	private static String[] directPlaySund = {"--no-audio"};

	private MediaPlayerFactory createFactory(){
		System.out.println("creating new MediaPlayerFactory...");

		String[] myargs = args;

		if(!applicationSettings.isPlaySoundDirectPlay())
			myargs = Lists.concat(args, directPlaySund);

		return new MediaPlayerFactory(myargs);
	}

	private void releasePlayer(){
		if(vlcMediaPlayer != null){ 
			vlcMediaPlayer.release();
			vlcMediaPlayer = null;
		}
	}

	private void releaseFactory(){
		if(vlcfactory != null){
			vlcfactory.release();
			vlcfactory = null;
		}
	}

	/**
	 * Callback to get the buffer format to use for video playback.
	 */
	private final BufferFormatCallback formatCallback = new BufferFormatCallback() {
		@Override
		public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
			int width;
			int height;

			width = (int)getWidth();
			height = (int)getHeight();

			System.out.println("MediaPlayerVLC:getBufferFormat: Media(" + width + " x " + height + ")");

			return new RV32BufferFormat(width, height);
		}
	};


	/**
	 * Callback to get the buffer format to use for video playback.
	 */
	private final RenderCallback renderCallback = new RenderCallback() {

		@Override
		public void display(DirectMediaPlayer mediaPlayer,
				Memory[] nativeBuffers, final BufferFormat bufferFormat) {
			final Memory nativeBuffer = nativeBuffers[0];
			final ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());

			//
			// JDK-8 requires that we access pixelwritter in FX-Thread
			//
			Platform.runLater(new Runnable () {
				@Override
				public void run() {
					pixelWriter.setPixels(0, 0, 
							bufferFormat.getWidth(),
							bufferFormat.getHeight(),
							pixelFormat, byteBuffer,
							bufferFormat.getWidth()*4);
				}
			});
		}
	};

}
