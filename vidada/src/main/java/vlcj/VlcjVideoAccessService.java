package vlcj;

import java.awt.image.BufferedImage;
import java.net.URI;

import vidada.model.video.IVideoAccessService;
import vidada.model.video.VideoInfo;
import archimedesJ.exceptions.NotSupportedException;


public class VlcjVideoAccessService implements IVideoAccessService {


	public VlcjVideoAccessService()
	{

	}


	@Override
	public boolean isAvaiable() {
		return VlcjUtil.isVlcjAvaiable();
	}

	@Override
	public VideoInfo extractVideoInfo(URI pathToVideFile) {
		throw new NotSupportedException("vlcj does not support video meta data access");
	}

	@Override
	public BufferedImage extractNativeFrame(URI pathToVideFile, int second) {
		throw new NotSupportedException("vlcj does not support video duration - use relative positions");
	}

	private final FrameExtractorVlcj extractor = new FrameExtractorVlcj();

	@Override
	public BufferedImage extractNativeFrame(URI pathToVideFile, final float position) {

		//
		// vlcj is not thread save
		// thus we have to synchronize the access
		//

		synchronized (extractor) {
			extractor.setMediaFile(pathToVideFile.toString());
			return extractor.extractFrameAt(position);
		}
	}

}
