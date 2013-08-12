package ffmpeg;

import java.io.File;

import org.joda.time.format.PeriodFormat;

import vidada.model.video.VideoInfo;
import ffmpeg.Interop.FFmpegInterop;

/**
 * Test suite ffmpeg 
 * @author IsNull
 *
 */
public class TestFFMpeg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FFmpegInterop fFmpeg = FFmpegInterop.instance();

		VideoInfo info = fFmpeg.getVideoInfo(new File("E:/MOVIE/moviestars-avatar-1080p.mkv").toURI());

		System.out.println(info.Duration.toPeriod().toString(PeriodFormat.getDefault()));
		System.out.println(info.Duration.getStandardSeconds());


		/*
		try {
			fFmpeg.createImage(Paths.get("E:/mov/nat.wmv"), Paths.get("E:/mov/thumbs and spaces/test.png"), 4, new Dimension(350,280));
		} catch (FFmpegException e) {
			e.printStackTrace();
		}*/
	}

}
