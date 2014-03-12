package vidada.model.video;


public class VideoAccessFactory {

	static IVideoAccessService videoAccessService;

	public static IVideoAccessService buildVideoAccessService(){
		if(videoAccessService == null){
			//
			// if possible, we use ffmpeg decoder for creating the thumbs
			// as an out process handling is much more verbose to errors 
			//
			videoAccessService =  new FFMpegVideoAccessService();
		}
		return videoAccessService;
	}
	
}
