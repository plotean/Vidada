package vidada.model.images.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.util.Debug;
import archimedesJ.util.Lists;

/**
 * Provides some image cache extension methods
 * 
 * @author IsNull
 *
 */
public class CacheUtils {


	/**
	 * Try to create a rescaled image of the given size. 
	 * 
	 * This method tries to abuse existing cached images to minimize 
	 * image processing and deliver maximal speed at high quality
	 * 
	 * @param imageCache The image cache
	 * @param id
	 * @param desiredSize
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static IMemoryImage getRescaledInstance(IImageCache imageCache, String id, Size desiredSize ){

		IMemoryImage sourceImage = null, resizedImage = null;

		Size size = getBestMatchingSize(imageCache, id, desiredSize);

		if(size != null)
		{
			// found a already rescaled image which is bigger
			System.out.println("getRescaledInstance: creating thumb from existing bigger thumb. Existing: " + size + " desired: " + desiredSize);

			sourceImage = imageCache.getImageById(id, size);
			if(sourceImage == null){
				System.err.println("getRescaledInstance: imageCache.getImageById = NULL for size: " +  size + ", id: " + id);
			}else {
				if(desiredSize.equals(size))
					return sourceImage; // the already cached image fits perfectly
			}
		}

		if(sourceImage != null){
			System.out.println("getRescaledInstance: rescaling source image to thumb. id: --> " + id);
			if(sourceImage != null)
				resizedImage =  sourceImage.rescale(desiredSize.width, desiredSize.height); //ScalrEx.rescaleImage(sourceImage, );
		}

		return resizedImage;
	}

	/**
	 * Returns a size of a already cached image which is as close to the desired size, but bigger or equal.
	 * 
	 * @param imageCache
	 * @param id
	 * @param desiredSize
	 * @return
	 */
	private static Size getBestMatchingSize(IImageCache imageCache, String id, Size desiredSize){

		Set<Size> dims = imageCache.getCachedDimensions(id);

		if(!dims.isEmpty()){

			List<Size> dimensions = Lists.toList(dims);
			Collections.sort(dimensions, new Comparator<Size>() {
				@Override
				public int compare(Size o1, Size o2) {
					return Double.compare(o1.width, o2.width) ;
				}
			});

			Debug.printAllLines("avaiable dimensions:", dimensions);

			int index = -1;
			for (int i = 0; i < dimensions.size(); i++) {
				if(dimensions.get(i).width >= desiredSize.width){
					index = i;
					break;
				}
			}
			if(index != -1 )
				return dimensions.get(index);
		}

		return null;
	}

}
