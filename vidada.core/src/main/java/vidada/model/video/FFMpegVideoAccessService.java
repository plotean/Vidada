package vidada.model.video;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import vidada.services.ServiceProvider;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import ffmpeg.FFmpegException;
import ffmpeg.Interop.FFmpegInterop;

public class FFMpegVideoAccessService implements IVideoAccessService {

	private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);
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
	public IMemoryImage extractNativeFrame(URI pathToVideFile, int second) {
		IMemoryImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			Size resoulution = info.NativeResolution;
			if(resoulution != null)
				frame = extractFrame(pathToVideFile, second, resoulution);
		}
		return frame; 
	}

	/**
	 * extractNativeFrame
	 */
	@Override
	public IMemoryImage extractNativeFrame(URI pathToVideFile, float position) {

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			if(info.NativeResolution != null){
				return extractFrame(pathToVideFile, position, info.NativeResolution);

			}else{
				System.err.println("extractNativeFrame: video info NativeResolution is unavaiable.");
			}
		}else{
			System.err.println("extractNativeFrame: video info could not be extracted!");
		}

		return null; 
	}

	@Override
	public IMemoryImage extractFrame(URI pathToVideFile, float position, Size frameSize) {
		position = Math.min(1f, Math.abs(position)); // ensure position is in valid range

		IMemoryImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			if(info.Duration != 0){
				int second =(int)((float)info.Duration * position);
				frame = extractFrame(pathToVideFile, second, frameSize);

			}else{
				System.err.println("extractFrame: video Duration info is unavaiable");
			}
		}else{
			System.err.println("extractFrame: video info could not be extracted!");
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
	private IMemoryImage extractFrame(URI pathToVideFile, int second, Size size){
		IMemoryImage frame = null;

		File pathToImage = null;

		try {
			pathToImage = File.createTempFile("thumb", ".png");
			try {
				ffmpeg.createImage(pathToVideFile, pathToImage, second, size);
				//load the file...			
				frame = imageFactory.createImage(pathToImage);

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
