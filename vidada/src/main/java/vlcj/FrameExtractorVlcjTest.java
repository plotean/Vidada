package vlcj;

import java.awt.image.BufferedImage;

public class FrameExtractorVlcjTest {


	/**
	 * test FrameExtractorVlcj
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		VLCjUtil.ensureVLCLib();

		extractor = new FrameExtractorVlcj();
		System.out.println("extractor ready!");

		System.out.println("setting media file...");
		extractor.setMediaFile("E:\\MOVIE\\looper.2012.720p.mkv");

		System.out.println("extracting frame 1...");
		BufferedImage vidfr = extractor.extractFrameAt(0.202f);

		System.out.println("extracting frame 2...");
		vidfr = extractor.extractFrameAt(0.3f);

		System.out.println("extracting frame 3...");
		vidfr = extractor.extractFrameAt(0.4f);

	}

	private static FrameExtractorVlcj extractor;





}
