package vidada.views.directplay;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;
import vidada.viewsFX.player.IMediaController;
import vlcj.VLCMediaController;
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

	private final VLCMediaController mediaController = new VLCMediaController();

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
		mediaController.bind(mediaPlayer);

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

	@Override
	public IMediaController getMediaController() {
		return mediaController;
	}

}
