package ffmpeg.Interop;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.joda.time.Duration;

import vidada.model.video.VideoInfo;
import archimedesJ.geometry.Size;
import archimedesJ.io.ShellExec;
import archimedesJ.util.OSValidator;
import archimedesJ.util.PackageUtil;
import ffmpeg.FFmpegException;

/**
 * Platform independent ffmpeg access
 * @author IsNull
 *
 */
public abstract class FFmpegInterop {

	protected static File encoder;

	private static FFmpegInterop instance = null;
	private static int DEFAULT_TIMEOUT = 1000 * 5; // ms


	public static FFmpegInterop instance(){

		if(instance == null){
			if(OSValidator.isWindows()){
				instance = new FFmpegInteropWindows();
			}else {
				instance = new FFmpegInteropLinux();
			}

			System.out.println("loaded ffmpeg interop: " + instance.getClass().getName());
		}
		return instance;
	}

	/**
	 * Extracts the ffmpeg binary for the current platform
	 * @param ffmpegPackagePath
	 * @return Returns the path to the ffmpeg binary
	 */
	protected static File extractFFMpeg(String ffmpegPackagePath) {

		File ffmpeg = null;

		try {
			URI jar = PackageUtil.getJarURI(FFmpegInteropWindows.class);
			URI ffmpegURI = PackageUtil.extractFile(jar, ffmpegPackagePath);

			ffmpeg = new File(ffmpegURI);
			ffmpeg.setExecutable(true);

			System.out.println("FFmpegInterop: Extracted ffmpeg to " + ffmpeg);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ffmpeg;
	}

	/**
	 *  Extracts the frame at the given time as an image.
	 *  
	 * @param pathToVideo
	 * @param pathToImage
	 * @param second
	 * @param size
	 * @throws FFmpegException
	 */
	public void createImage(URI pathToVideo, File pathToImage, int second, Size size) throws FFmpegException {

		FileUtils.deleteQuietly(pathToImage);

		List<String> argumentBuilder = new ArrayList<String>();

		argumentBuilder.add("-ss"); 		// seek - better before setting the -i input! 
		argumentBuilder.add(second +"");

		argumentBuilder.add("-i");			// input video
		argumentBuilder.add(shieldPathArgument(pathToVideo));

		argumentBuilder.add("-an");			// no audio

		argumentBuilder.add("-frames:v");	// frames to extract
		argumentBuilder.add("1");

		argumentBuilder.add("-y");			// overwrite existing thumb

		// thumb size
		argumentBuilder.add("-vf");
		// the following will scale the thumb to the desired size but not stretch the image
		argumentBuilder.add("scale=max("+size.width+"\\,a*"+size.height+"):max("+size.height+"\\,"+size.width+"/a),crop="+size.width+":"+size.height);

		argumentBuilder.add(shieldPathArgument(pathToImage));

		String log = ffmpegExec(argumentBuilder, DEFAULT_TIMEOUT);

		if(!pathToImage.exists())
		{
			throw new FFmpegException("Image could not been created.(file missing)", log);
		}
	}	


	//
	// define the patterns to extract the information
	//
	private static final Pattern regex_Duration = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d\\d)");
	private static final Pattern regex_BitRate = Pattern.compile("bitrate: (\\d*) kb/s");
	private static final Pattern regex_Resolution = Pattern.compile("Video: .* (\\d{2,})x(\\d{2,})");

	public VideoInfo getVideoInfo(URI pathToVideo) {

		//if(!pathToVideo.exists())
		//	throw new IllegalArgumentException("file must exist and being readable! @ " + pathToVideo.toString());

		List<String> argumentBuilder = new ArrayList<String>();

		argumentBuilder.add("-ss"); 		// seek - 
		argumentBuilder.add("2");

		argumentBuilder.add("-i");
		argumentBuilder.add(shieldPathArgument(pathToVideo));

		argumentBuilder.add("-an");			// no audio

		argumentBuilder.add("-frames:v");	// frames to extract
		argumentBuilder.add("1");

		String log = ffmpegExec(argumentBuilder, DEFAULT_TIMEOUT);

		Duration videoDuration = null;
		int videoBitrate = 0;
		Size resolution = null;


		//
		// parse duration
		//
		Matcher m = regex_Duration.matcher(log);
		if(m.find()){
			// Duration: 02:41:41.68,

			Duration hours = Duration.standardHours(Integer.parseInt(m.group(1)));
			Duration minutes = Duration.standardMinutes(Integer.parseInt(m.group(2)));
			Duration seconds = Duration.standardSeconds(Integer.parseInt(m.group(3)));

			videoDuration = Duration.millis(hours.getMillis() + minutes.getMillis() + seconds.getMillis());
		}else {
			System.err.println("duration info not found!");
		}

		//
		// parse duration
		//
		m = regex_BitRate.matcher(log);
		if(m.find()){
			// bitrate: 10731 kb/s
			videoBitrate = Integer.parseInt(m.group(1));

		}else {
			System.err.println("bitrate info not found!");
		}


		//
		// parse native resolution
		//
		m = regex_Resolution.matcher(log);
		if(m.find()){
			resolution = new Size( 
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)));
		}else {
			System.err.println("resolution info not found!");
		}

		return new VideoInfo((int)videoDuration.getStandardSeconds(), videoBitrate, resolution);
	}


	/**
	 * Execute the given ffmpeg command
	 * @param args
	 * @param timeout
	 * @return
	 */
	private String ffmpegExec(List<String> args, long timeout){

		StringBuilder output = new StringBuilder();

		try {
			args.add(0, getFFmpegCMD());
			String[] command = args.toArray(new String[0]);
			System.out.println("FFmpegInterop: running ShellExecute.executeAndWait ...");

			int exitVal = ShellExec.executeAndWait(command, output, true, timeout);

		} catch( InterruptedException e){
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

		System.out.println("FFmpegInterop: ffmpeg done.");

		return output.toString();
	} 

	protected String dimensionToString(Size size){
		return  size.width + "x" + size.height;
	}

	protected String shieldPathArgument(File pathArg){
		return "\"" + pathArg.getAbsolutePath() + "\"";
	}

	protected String shieldPathArgument(URI pathArg){
		return "\"" + pathArg.getPath() + "\"";
	}

	/**
	 * Is ffmpeg avaiable
	 * @return
	 */
	public abstract boolean isAvaiable();

	public abstract File getFFmpegBinaryFile();

	protected abstract String getFFmpegCMD();




}
