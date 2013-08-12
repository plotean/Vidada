package ffmpeg.Interop;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipException;

import archimedesJ.util.PackageUtil;

/**
 * frame=    0 fps=0.0 q=0.0 size=N/A time=00:00:00.00 bitrate=N/A    
 *
 *??
 * @author IsNull
 *
 */
class FFmpegInteropLinux extends FFmpegInterop
{
	private static File encoder;

	static {
		encoder = extractFFMpeg();
	}

	@Override
	public File getFFmpegBinaryFile() {
		return encoder;
	}

	private static File extractFFMpeg() {

		File ffmpeg = null;

		try {
			URI jar = PackageUtil.getJarURI(FFmpegInteropWindows.class);
			URI ffmpegURI = PackageUtil.extractFile(jar, "tools/ffmpeg");

			ffmpeg = new File(ffmpegURI);
			ffmpeg.setExecutable(true);

			System.out.println("extracted ffmpeg to " + ffmpeg);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ffmpeg;
	}

	@Override
	public String getFFmpegCMD() {
		return shieldPathArgument(getFFmpegBinaryFile()); 		//return "ffmpeg";
	}

	@Override
	protected String shieldPathArgument(File pathArg){
		return pathArg.getAbsolutePath();
	}

	@Override
	protected String shieldPathArgument(URI pathArg){
		return pathArg.getPath();
	}

	boolean isAvaiable;
	boolean isAvaiableDirty = true;

	@Override
	public boolean isAvaiable() {
		if(isAvaiableDirty){
			isAvaiable = getFFmpegBinaryFile().exists();
			isAvaiableDirty = false;
		}

		return isAvaiable;
	}

}
