package vidada.model.video;

import java.awt.Dimension;

import org.joda.time.Duration;

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
	public final Dimension NativeResolution;
	
	
	public boolean hasAllInfos(){
		return Duration != null && BitRate != 0 && NativeResolution != null;
	}
	
	
	public VideoInfo(Duration duration, int bitrate, Dimension resolution){
		this.Duration = duration;
		this.BitRate = bitrate;
		this.NativeResolution = resolution;
	}
}
