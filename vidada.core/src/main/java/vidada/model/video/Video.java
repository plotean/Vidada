package vidada.model.video;

import java.awt.image.BufferedImage;
import java.io.IOException;

import archimedesJ.io.locations.IResourceAccessContext;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Represents a Video-File
 * @author IsNull
 *
 */
public class Video {

	private static IVideoAccessService videoAccessService = VideoAccessFactory.buildVideoAccessService();


	private ResourceLocation videoResource;

	private VideoInfo videoInfo = null;


	public Video(ResourceLocation videoResource){
		this.videoResource = videoResource;
	}


	public void setPathToVideoFile(ResourceLocation pathToVideFile){
		this.videoResource = pathToVideFile;
		this.videoInfo = null;
	}

	/**
	 * Returns the VideoInfo
	 * @return
	 */
	public VideoInfo getVideoInfo(){
		if(videoInfo == null){
			IResourceAccessContext ctx = videoResource.openResourceContext();
			try{
				videoInfo = videoAccessService.extractVideoInfo(ctx.getUri());
			}finally{
				try {
					ctx.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return videoInfo;
	}

	public static boolean isGenericEncoderPresent(){
		return videoAccessService.isAvaiable();
	}

	/**
	 * Gets the frame at the given second in its native resolution
	 * 
	 * @param second
	 * @return
	 */
	public BufferedImage getNativeFrame(int second){
		IResourceAccessContext ctx = videoResource.openResourceContext();
		try{
			return videoAccessService.extractNativeFrame(ctx.getUri(), second); 
		}finally{
			try {
				ctx.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the frame at the given position in its native resolution
	 * 
	 * @param position 0.0 - 1.0 Relative position in the video
	 * @return
	 */
	public BufferedImage getNativeFrame(float position){
		IResourceAccessContext ctx = videoResource.openResourceContext();
		try{
			return videoAccessService.extractNativeFrame(ctx.getUri(), position); 
		}finally{
			try {
				ctx.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
