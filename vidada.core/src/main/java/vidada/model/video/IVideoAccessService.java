package vidada.model.video;

import java.awt.image.BufferedImage;
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
	public abstract BufferedImage extractNativeFrame(URI pathToVideFile, int second);

	/**
	 * Extracts a frame from the given relative position
	 * @param position 0.0-1.0 Relative position, 0.5 would be in the middle of the movie
	 * @return
	 */
	public abstract BufferedImage extractNativeFrame(URI pathToVideFile, float position);

}
