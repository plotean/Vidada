package vlcj;

import java.awt.image.BufferedImage;
import java.net.URI;

import vidada.model.video.IVideoAccessService;
import vidada.model.video.VideoInfo;
import archimedesJ.exceptions.NotSupportedException;


/**
 * 
 * 
 * @author IsNull
 *
 */
public class VlcjDirectVideoAccessService implements IVideoAccessService {

	//private final MediaPlayerFactory factory;

	public VlcjDirectVideoAccessService(){
		//String[] args = {"--no-plugins-cache", "--no-video-title-show", "--no-snapshot-preview", "--quiet", "--quiet-synchro", "--intf", "dummy"};
		//factory = new MediaPlayerFactory(args);
	}



	@Override
	public BufferedImage extractNativeFrame(URI pathToVideFile, float position) {
		BufferedImage frame;

		FrameExtractorVlcj fExtractor = new FrameExtractorVlcj();

		fExtractor.setMediaFile(pathToVideFile.toString());

		frame = fExtractor.extractFrameAt(position);

		return frame;
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
}
