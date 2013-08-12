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
			
			// we cant use vlcj as it is not stable enough
			
			/*
			if(FFmpegInterop.instance().isAvaiable())
			{
				videoAccessService =  new FFMpegVideoAccessService();
				System.out.println("using ffmpeg");
			}else{
				// fall back to vlcj
				videoAccessService =  new VlcjVideoAccessService(); 
				System.out.println("using vlcj");
			}
			*/
		}
		return videoAccessService;
	}
	
}
