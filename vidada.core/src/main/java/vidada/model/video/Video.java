package vidada.model.video;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.io.locations.IResourceAccessContext;
import archimedes.core.io.locations.ResourceLocation;

import java.io.IOException;

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


	protected void setPathToVideoFile(ResourceLocation pathToVideFile){
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
		return videoAccessService.isAvailable();
	}


	/**
	 * Gets the frame at the given position in its native resolution
	 * 
	 * @param position 0.0 - 1.0 Relative position in the video
	 * @return
	 */
	public IMemoryImage getNativeFrame(float position){
		return getFrame(position, null);
	}

	/**
	 * Gets the frame at the given position in the requested resolution
	 * @param position
	 * @param thumbSize
	 * @return
	 */
	public IMemoryImage getFrame(float position, Size thumbSize){
		IResourceAccessContext ctx = videoResource.openResourceContext();
		try{
			return videoAccessService.extractFrame(ctx.getUri(), position, thumbSize); 
		}finally{
			try {
				ctx.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
