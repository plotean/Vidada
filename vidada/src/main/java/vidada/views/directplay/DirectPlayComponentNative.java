package vidada.views.directplay;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;
import vlcj.VlcjUtil;
import archimedesJ.util.OSValidator;


/**
 * DirectPlayComponent
 * 
 * Directly playing movies with an vlc component.
 * Beyond the facade it uses vlcj.
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class DirectPlayComponentNative extends DirectPlayBaseComponent {


	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;

	public DirectPlayComponentNative(){

		//mediaPlayerComponent = new EmbeddedMediaPlayerComponent();	

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent(){
			private static final long serialVersionUID = 1L;


			@Override
			protected Canvas onGetCanvas() {
				Canvas canvas = null;
				if(OSValidator.isWindows())
					canvas = new WindowsCanvas();
				else {
					canvas = new Canvas();
				}
				canvas.setBackground(Color.black);
				return canvas;

			}
		};


		mediaPlayer = mediaPlayerComponent.getMediaPlayer();

		setLayout(new BorderLayout());

		add(mediaPlayerComponent, BorderLayout.CENTER);


		revalidate();
	}

	static {
		VlcjUtil.ensureVLCLib();
	}


	public EmbeddedMediaPlayerComponent getEmbeddedMediaPlayerComponent(){
		return mediaPlayerComponent;
	}

	/*
	private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {

		@Override
		public void mouseDragged(MouseEvent e) { }

		@Override
		public void mouseMoved(MouseEvent e) {
			float xpos = e.getX();
			float width = e.getComponent().getSize().width;
			float relativeXPos = 1 / width * xpos;

			mediaPlayer.setPosition(relativeXPos);
		}
	};

	private final MouseListener mouseEventListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("clicled");
		}
	};
	 */

	@Override
	public void setCropGeometry(String aspectRatio) {
		mediaPlayer.setCropGeometry(aspectRatio);
	}

	@Override
	public void playMedia(String file) {
		String[] strings = {};
		mediaPlayer.playMedia(file, strings);
	}

	@Override
	public void setPosition(float pos) {
		if(mediaPlayer != null){
			mediaPlayer.setPosition(pos);
		}
	}

	@Override
	public void stop() {
		if(mediaPlayer != null){
			mediaPlayer.stop();
		}
	}

	@Override
	public float getPosition() {
		float pos = 0;
		if(mediaPlayer != null){
			pos = mediaPlayer.getPosition();
		}

		return pos;
	}

	@Override
	public void ensurePlayerSize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScale(float factor) {
		if(mediaPlayer != null){
			mediaPlayer.setScale(factor);
		}
	}

	@Override
	public void pause() {
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}
	}


}
