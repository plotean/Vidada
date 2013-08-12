package vlcj;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

import com.sun.jna.Memory;

/**
 * Allows extraction of frames from a video file
 * @author IsNull
 *
 */
public class FrameExtractorDirectVlcj {


	//private final int RENDER_WIDTH = 1920;
	//private final int RENDER_HEIGHT = 1080;

	//private final int RENDER_WIDTH = 1280;
	//private final int RENDER_HEIGHT = 720;

	private final int RENDER_WIDTH = 400;
	private final int RENDER_HEIGHT = 400;

	private DirectMediaPlayer mediaPlayer;
	private BufferedImage image;
	private VideoSurfacePane videoSurfacePane;


	public VideoSurfacePane getPane(){
		return videoSurfacePane;
	}

	private static MediaPlayerFactory createMediaPlayerFactory(){
		String[] vlcArgs = {"--no-plugins-cache", "--no-video-title-show", "--no-snapshot-preview", "--quiet", "--quiet-synchro", "--intf", "dummy"};
		MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
		return factory;
	}

	public FrameExtractorDirectVlcj(){
		this(createMediaPlayerFactory());
	}

	public FrameExtractorDirectVlcj(MediaPlayerFactory factory){

		mediaPlayer = factory.newDirectMediaPlayer(RENDER_WIDTH , RENDER_HEIGHT, new FrameExtractorCallback());
	}


	private DirectMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public BufferedImage extractFrameAt(String pathToVideFile, float position){
		String file = pathToVideFile.toString();

		getMediaPlayer().playMedia(file);
		mediaPlayer.setPosition(position);

		return getImage();
	}

	private BufferedImage getImage() {
		return image;
	}





	private final class FrameExtractorCallback implements RenderCallback {

		private BufferedImage image;
		private final int[] imageBuffer;
		private int num = 0;

		public FrameExtractorCallback() {
			image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration().createCompatibleImage(RENDER_WIDTH , RENDER_HEIGHT); //nativeSize.width, nativeSize.height); //500, 500
			image.setAccelerationPriority(1.0f);
			imageBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		}

		@SuppressWarnings("unused")
		public BufferedImage getFrame(){
			return image; 
		}


		@SuppressWarnings("unused")
		@Override
		public synchronized void display(Memory nativeBuffer) {

			System.out.println("nativeBuffer size:" + nativeBuffer.size());

			DirectMediaPlayer player = getMediaPlayer();


			//rgbBuffer = new int[(int) nativeBuffer.size() / 4];		
			//nativeBuffer.read(0, rgbBuffer, 0, rgbBuffer.length);


			Dimension nativeSize = player.getVideoDimension();

			System.out.println("position " + player.getPosition());
			System.out.println(nativeSize.width + " / " + nativeSize.height);


			int size = (int) nativeBuffer.size() / 4;
			nativeBuffer.read(0, imageBuffer, 0, imageBuffer.length);
			//player.stop();

			// debug purposes
			String rndName = ++num + "_test.png";
			persist(image, new File("E:\\Out", rndName) );
		}
	}

	/**
	 * Writes the given image to disk
	 * @param image
	 * @param path
	 */
	private void persist(BufferedImage image, File path){
		try {

			path.mkdirs();

			ImageIO.write(image, "png", path);
			System.out.println("image written to " + path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
