package ffmpeg.Interop;


import java.io.File;

class FFmpegInteropWindows extends FFmpegInterop
{
	static {
		encoder = extractFFMpeg("tools/ffmpeg.exe");
	}


	@Override
	public File getFFmpegBinaryFile() {
		return encoder;
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