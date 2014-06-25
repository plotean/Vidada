package vidada.model.video;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;

import java.net.URI;

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
	public boolean isAvailable();


	/**
	 * Extracts video informations
	 * @param pathToVideoFile
	 * @return
	 */
	public abstract VideoInfo extractVideoInfo(URI pathToVideoFile);

	/**
	 * Extracts a frame from at the given second
	 * @param second
	 * @return
	 */
	public abstract IMemoryImage extractNativeFrame(URI pathToVideoFile, int second);

	/**
	 * Extracts a frame in original size from the given relative position
	 * @param position 0.0-1.0 Relative position, 0.5 would be in the middle of the movie
	 * @return
	 */
	public abstract IMemoryImage extractNativeFrame(URI pathToVideoFile, float position);

	/**
	 * Extracts a frame in original size from the given relative position
	 * @param pathToVideoFile
	 * @param position 0.0-1.0 Relative position, 0.5 would be in the middle of the movie
	 * @param frameSize The requested thumb size
	 * @return
	 */
	public abstract IMemoryImage extractFrame(URI pathToVideoFile, float position, Size frameSize);

}
