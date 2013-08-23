package vidada.model.video;

import org.joda.time.Duration;

import archimedesJ.geometry.Size;

public class VideoInfo {

	/**
	 * Gets the duration of this video
	 */
	public final Duration Duration;

	/**
	 * Gets the bitrate in kb/s
	 */
	public final int BitRate;

	/**
	 * Gets the native video resolution
	 */
	public final Size NativeResolution;


	public boolean hasAllInfos(){
		return Duration != null && BitRate != 0 && NativeResolution != null;
	}


	public VideoInfo(Duration duration, int bitrate, Size resolution){
		this.Duration = duration;
		this.BitRate = bitrate;
		this.NativeResolution = resolution;
	}
}
