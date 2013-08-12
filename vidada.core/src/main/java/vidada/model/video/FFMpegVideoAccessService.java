package vidada.model.video;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import ffmpeg.FFmpegException;
import ffmpeg.Interop.FFmpegInterop;

public class FFMpegVideoAccessService implements IVideoAccessService {

	private final static FFmpegInterop ffmpeg = FFmpegInterop.instance();	
	private final Map<String, VideoInfo> videoInfoCache = new HashMap<String, VideoInfo>();


	@Override
	public VideoInfo extractVideoInfo(URI pathToVideFile) {
		String pathstr = pathToVideFile.toString();
		if(!videoInfoCache.containsKey(pathstr))
		{
			VideoInfo info = ffmpeg.getVideoInfo(pathToVideFile);
			if(info.hasAllInfos())
				videoInfoCache.put(pathstr, info);
		}
		return videoInfoCache.get(pathstr);
	}


	@Override
	public BufferedImage extractNativeFrame(URI pathToVideFile, int second) {
		BufferedImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			Dimension resoulution = info.NativeResolution;
			if(resoulution != null)
				frame = extractFrame(pathToVideFile, second, resoulution);
		}
		return frame; 
	}

	/**
	 * extractNativeFrame
	 */
	@Override
	public BufferedImage extractNativeFrame(URI pathToVideFile, float position) {

		position = Math.min(1f, Math.abs(position)); // ensure position is in valid range

		BufferedImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			if(info.NativeResolution != null && info.Duration != null){

				int second =(int)((float)info.Duration.getStandardSeconds() * position);
				frame = extractFrame(pathToVideFile, second, info.NativeResolution);

			}else{
				System.err.println("video info is partially unavaiable");
			}
		}else{
			System.err.println("video info could not be extracted!");
		}

		return frame; 
	}


	/**
	 * Returns a frame of this video at the given position and with the given size
	 * 
	 * @param second
	 * @param size
	 * @return returns a bufferedimage representing the frame
	 */
	private BufferedImage extractFrame(URI pathToVideFile, int second, Dimension size){
		BufferedImage frame = null;

		File pathToImage = null;

		try {
			pathToImage = File.createTempFile("thumb", ".png");
			try {
				ffmpeg.createImage(pathToVideFile, pathToImage, second, size);
				//load the file...

				try {
					frame = ImageIO.read(pathToImage);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (FFmpegException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			// remove the temporary image
			FileUtils.deleteQuietly(pathToImage);
		}

		return frame;	
	}

	@Override
	public boolean isAvaiable() {
		return ffmpeg.isAvaiable();
	}


}
