package vidada.mock;

import vidada.model.media.ImageMediaItem;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaDataCloner {


	public static List<MediaItem> crowd(List<MediaItem> medias, int clones) {
		List<MediaItem> moreMedias = new ArrayList<MediaItem>(medias.size() * clones);
		for (MediaItem mediaData : medias) {
			for (int i = 0; i < clones; i++) {
				MediaItem clone = mockClone(mediaData, "C" + i + " ");
				moreMedias.add(clone);
			}
			moreMedias.add(mediaData);
		}
		return moreMedias;
	}


	public static List<MediaItem> crowdRandom(List<MediaItem> medias, int clones) {
		int populationSize = medias.size() * clones;
		List<MediaItem> moreMedias = new ArrayList<MediaItem>(populationSize);

		Random rnd = new Random();
		MediaItem clone;
		for (int i = 0; i < populationSize; i++) {
			clone = mockClone(
					medias.get(rnd.nextInt(medias.size()-1)), 
					"C" + rnd.nextInt(9999) + " ");
			moreMedias.add(clone);
		}

		return moreMedias;
	}


	/**
	 * Creates a copy of the given mediaData
	 * 
	 * @param mediaData
	 * @param prefix
	 * @return
	 */
	private static MediaItem mockClone(MediaItem mediaData, String prefix){
		MediaItem clone = null;
		if(mediaData instanceof MovieMediaItem)
		{
			MovieMediaItem movie = (MovieMediaItem)mediaData;

			clone = new MovieMediaItem(movie);
			clone.setFilename(prefix + clone.getFilename());
		}else if(mediaData instanceof ImageMediaItem)
		{
			ImageMediaItem movie = (ImageMediaItem)mediaData;
			clone = new ImageMediaItem(movie);
			clone.setFilename(prefix + clone.getFilename());
		}
		return clone;
	}

}
