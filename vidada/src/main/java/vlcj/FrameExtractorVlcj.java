package vlcj;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * 
 * @author IsNull
 *
 */
public class FrameExtractorVlcj {

	private static final String[] VLC_ARGS = {
        "--intf", "dummy",          /* no interface */
        "--vout", "dummy",          /* we don't want video (output) */
        "--no-audio",               /* we don't want audio (decoding) */
        "--no-video-title-show",    /* nor the filename displayed */
        "--no-stats",               /* no stats */
        "--no-sub-autodetect-file", /* we don't want subtitles */
        "--no-disable-screensaver", /* we don't want interfaces */
        "--no-snapshot-preview",    /* no blending in dummy vout */
    };
	
	private static MediaPlayerFactory factory = new MediaPlayerFactory(VLC_ARGS);
	
	private  MediaPlayer mediaPlayer;
	private String mediaPath;
	
	public FrameExtractorVlcj(){
		 mediaPlayer = factory.newHeadlessMediaPlayer();
	}
	
	public synchronized void setMediaFile(String pathToVideFile) {
		mediaPath = pathToVideFile;
	}
	
	
	@Override
	public void finalize(){
		release();
	}
	
	/**
	 * release the native components
	 */
	public synchronized void release(){
		if(mediaPlayer != null) mediaPlayer.release();
	}
	

	public synchronized BufferedImage extractFrameAt(final float position) {
			BufferedImage frame = null;
		
	        String mrl = mediaPath;


	        File snapshotFile;
			try {
				snapshotFile = File.createTempFile("thumb", ".png");
			} catch (IOException e1) {
				System.err.println("temporary snapshot file could not be created.");
				return null;
			}

	        final CountDownLatch inPositionLatch = new CountDownLatch(1);
	        final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);
	        
	        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

	            @Override
	            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
	                if(newPosition >= position * 0.9f) { /* 90% margin */
	                    inPositionLatch.countDown();
	                }
	            }
	            
	            @Override
	            public void error(MediaPlayer mediaPlayer) {
	            	 inPositionLatch.countDown();
	            	 snapshotTakenLatch.countDown();
	            }

	            @Override
	            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
	                snapshotTakenLatch.countDown();
	            }
	        });

	        try{	        	
			    if(mediaPlayer.startMedia(mrl)) {
			        mediaPlayer.setPosition(position);
			        snapshotTakenLatch.await(2, TimeUnit.SECONDS); 
			        mediaPlayer.pause();
			        
			        mediaPlayer.saveSnapshot(snapshotFile);
			        snapshotTakenLatch.await(2, TimeUnit.SECONDS); 
			        mediaPlayer.stop();
			        
			        //
			        //load the image from disk...
			        //
			        
					try {
						frame = ImageIO.read(snapshotFile);
					} catch (IOException e) {
						e.printStackTrace();
					}

			    }
	        
	        }catch(InterruptedException e){
	        	System.err.println("native frame could not be extracted");
	        	//e.printStackTrace();
	        }finally{
	        	FileUtils.deleteQuietly(snapshotFile);
	        }
		
		return frame;
	}
	

}
