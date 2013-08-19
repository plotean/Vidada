package vidada.model.video;

import java.net.URI;

import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Provides access to a video, such as extracting metadata and creating snapshots
 * @author IsNull
 *
 */
public interface IVideoAccessService {

	/**
	 * Is the native video access available
	 * @return
	 */
	public boolean isAvaiable();


	/**
	 * Extracts video informations
	 * @param pathToVideFile
	 * @return
	 */
	public abstract VideoInfo extractVideoInfo(URI pathToVideFile);

	/**
	 * Extracts a frame from at the given second
	 * @param second
	 * @return
	 */
	public abstract IMemoryImage extractNativeFrame(URI pathToVideFile, int second);

	/**
	 * Extracts a frame in original size from the given relative position
	 * @param position 0.0-1.0 Relative position, 0.5 would be in the middle of the movie
	 * @return
	 */
	public abstract IMemoryImage extractNativeFrame(URI pathToVideFile, float position);

	/**
	 * Extracts a frame in original size from the given relative position
	 * @param pathToVideFile
	 * @param position 0.0-1.0 Relative position, 0.5 would be in the middle of the movie
	 * @param frameSize The requested thumb size
	 * @return
	 */
	public abstract IMemoryImage extractFrame(URI pathToVideFile, float position, Size frameSize);

}
