package vidada;

import java.awt.BorderLayout;
import java.awt.Button;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Ignore;

import vidada.views.directplay.DirectPlayComponentNative;

public class DirectPlayTest {

	/**
	 * @param args
	 */
	@Ignore
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DirectPlayTest();
			}
		});
	}



	private DirectPlayComponentNative directplayComponent;

	public DirectPlayTest(){
		//player("E:\\MOVIE\\Skyfall.2012.TS.XViD.avi");
		player("/Users/IsNull/Movies/looper.720p.x264.mkv");
	}


	private void player(String file) {

		JFrame frame = new JFrame("vlcj Tutorial");


		directplayComponent = new DirectPlayComponentNative();

		frame.setLocation(100, 100);
		frame.setSize(1050, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		Button button = new Button("New button");
		frame.getContentPane().add(button, BorderLayout.WEST);

		Button button_1 = new Button("New button");
		frame.getContentPane().add(button_1, BorderLayout.NORTH);
		frame.setVisible(true);

		//EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();

		frame.getContentPane().add(directplayComponent,BorderLayout.CENTER);

		//MediaPlayer mediaPlayer = directplayComponent.getMediaPlayer();
		//mediaPlayer.enableOverlay(true);
		directplayComponent.getMediaController().playMedia(file);


		//directplayComponent.doit();

		//EmbeddedMediaPlayerComponent mediaPlayerComponent= directplayComponent.getEmbeddedMediaPlayerComponent();




	}

}
