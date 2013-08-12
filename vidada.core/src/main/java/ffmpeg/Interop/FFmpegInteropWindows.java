package ffmpeg.Interop;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipException;

import archimedesJ.util.PackageUtil;

class FFmpegInteropWindows extends FFmpegInterop
{
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
			URI ffmpegURI = PackageUtil.extractFile(jar, "tools/ffmpeg.exe");

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
		return shieldPathArgument(getFFmpegBinaryFile());
	}

	@Override
	public boolean isAvaiable() {
		return getFFmpegBinaryFile().exists();
	}
}