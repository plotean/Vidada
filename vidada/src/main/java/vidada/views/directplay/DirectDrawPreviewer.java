package vidada.views.directplay;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import vidada.model.settings.DatabaseSettings;
import vlcj.VideoSurfacePane;
import vlcj.VlcjUtil;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.swing.redirectionEvents.EventRedirectionSupport;
import archimedesJ.util.Lists;

/**
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class DirectDrawPreviewer extends DirectPlayBaseComponent {

	private final DatabaseSettings applicationSettings = DatabaseSettings.getSettings();
	private final Object resourceLock = new Object();
	private MediaPlayerFactory factory;

	private String lastPlayedMedia;

	private VideoSurfacePane _videoSurface;
	protected DirectMediaPlayer _mediaPlayer;
	protected Dimension mediaPlayerSize = new Dimension();

	static {
		VlcjUtil.ensureVLCLib();
	}

	public DirectDrawPreviewer(){

		// The size does NOT need to match the mediaPlayer size - it's the size that
		// the media will be scaled to
		// Matching the native size will be faster of course

		createFactory();

		this.setLayout(new BorderLayout());

		System.out.println("getPlaySoundDirectPlayChanged: " + applicationSettings.getPlaySoundDirectPlayChanged());
		applicationSettings.getPlaySoundDirectPlayChanged().add(new EventListenerEx<EventArgs>() {

			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				createFactory();
			}
		});
	}


	private void createFactory(){
		System.out.println("creating new MediaPlayerFactory...");

		if(factory != null){
			factory.release();
			factory = null;
		}

		String[] args = {"--no-plugins-cache",  "--no-video-title-show", "--no-snapshot-preview", "--quiet", "--quiet-synchro", "--intf", "dummy"};
		String[] directPlaySund = {"--no-audio"};

		if(!applicationSettings.isPlaySoundDirectPlay())
			args = Lists.concat(args, directPlaySund);

		for(String arg : args){
			System.out.print(arg.toString());
			System.out.print(", ");
		}
		System.out.println("");

		factory = new MediaPlayerFactory(args);
		_mediaPlayer = null;
	}

	private final DirectMediaPlayer getMediaPlayer(){
		int width = getWidth();
		int height = getHeight();

		if(_mediaPlayer == null || 
				mediaPlayerSize.getWidth() != width ||
				mediaPlayerSize.getHeight() != height)
		{
			if(_mediaPlayer != null){
				System.out.println("new desired size " + width + "/" + height);
				System.out.println("old mediaplayer size"+ mediaPlayerSize );
			}else {
				System.out.println("new media player old was NULL");
			}

			cleanUp();
			_mediaPlayer = createMediaPlayer(width, height);
			mediaPlayerSize.width = width;
			mediaPlayerSize.height = height;
		}

		return _mediaPlayer;
	}


	protected DirectMediaPlayer createMediaPlayer(int width, int height){
		BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		image.setAccelerationPriority(1.0f);

		_videoSurface = createImagePane(image);
		_videoSurface.addMouseMotionListener(mouseMotionListener);

		this.add(_videoSurface, BorderLayout.CENTER);

		_mediaPlayer = factory.newDirectMediaPlayer(width, height, createRenderer(_videoSurface));

		EventRedirectionSupport.redirectAllMouseEvents(_videoSurface, this);
		EventRedirectionSupport.redirectKeyEvents(_videoSurface, this);

		this.repaint();

		System.out.println("new media player created: " +  width + "/"+ height);

		return _mediaPlayer;
	}



	protected VideoSurfacePane createImagePane(BufferedImage image){
		VideoSurfacePane videoSurface = new VideoSurfacePane(image, getVideoOverlayRenderer());
		return videoSurface;
	}


	protected RenderCallback createRenderer(VideoSurfacePane pane){
		return new RedrawRenderCallback(pane);
	}




	private final MouseMotionListener mouseMotionListener = new MouseAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			float xpos = e.getX();
			float width = e.getComponent().getSize().width;
			float relativeXPos = 1 / width * xpos;

			//System.out.println(relativeXPos);
			MediaPlayer mediaPlayer = _mediaPlayer;
			if(mediaPlayer != null){
				mediaPlayer.setPosition(relativeXPos);
			}
		}
	};


	@Override
	public void playMedia(String file) {
		lastPlayedMedia = file;
		// vlc(j) path fix
		file = file.replace("file:/", "file:///");

		boolean success = getMediaPlayer().playMedia(file);
		System.out.println("playMedia succeeded? " + success + " - playing file: " + file);
		this.validate();
		this.invalidate();
		this.repaint();
	}

	@Override
	public void ensurePlayerSize() {

		if(lastPlayedMedia != null)
		{
			float lastPos = getPosition();

			playMedia(lastPlayedMedia);
			setPosition(lastPos);
		}

	}


	private final class RedrawRenderCallback extends RenderCallbackAdapter {
		private final VideoSurfacePane imagePane;

		public RedrawRenderCallback(VideoSurfacePane imagePane) {
			super(((DataBufferInt) imagePane.getImage().getRaster().getDataBuffer()).getData());
			this.imagePane = imagePane;
		}

		@Override
		protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
			imagePane.repaint();
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
	public void pause(){
		lastPlayedMedia = null;
		MediaPlayer mediaPlayer = _mediaPlayer;
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}else{
			System.err.println("media player not avaiable!");
		}
	}

	@Override
	public void finalize(){
		release();
	}


	private synchronized void release() {
		synchronized (resourceLock) {
			if(factory != null){
				factory.release();
				factory = null;
			}
		}

		cleanUp();
	}


	private void cleanUp(){
		synchronized (resourceLock) {
			if(_videoSurface != null){
				this.remove(_videoSurface); // remove imagePane
				_videoSurface = null;
			}

			if(_mediaPlayer != null)
			{
				_mediaPlayer.release();
				_mediaPlayer = null;
			}
		}
	}

}
