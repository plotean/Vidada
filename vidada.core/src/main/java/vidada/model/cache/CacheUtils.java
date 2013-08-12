package vidada.model.cache;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import archimedesJ.geometry.Size;
import archimedesJ.swing.images.AsyncImage;
import archimedesJ.util.Debug;
import archimedesJ.util.Lists;
import archimedesJ.util.images.ScalrEx;

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
	public static BufferedImage getRescaledInstance(IImageCacheService imageCache, String id, Size desiredSize ){

		BufferedImage resizedImage = null;

		List<Size> dimensions = Lists.toList(imageCache.getCachedDimensions(id));
		Collections.sort(dimensions, new Comparator<Size>() {
			@Override
			public int compare(Size o1, Size o2) {
				return Double.compare(o1.width, o2.width) ;
			}
		});

		Debug.printAllLines("avaiable dimensions:", dimensions);

		int index = -1;
		for (int i = 0; i < dimensions.size(); i++) {
			if(dimensions.get(i).width > desiredSize.width){
				index = i;
				break;
			}
		}

		Image sourceImage = null;
		if(index != -1)
		{
			// found a already rescaled image
			Size size = dimensions.get(index);
			System.out.println("getRescaledInstance: creating thumb from existing bigger thumb. Existing: " + size + " desired: " + desiredSize);

			sourceImage = imageCache.getImageById(id, size);
			if(sourceImage == null)
				System.err.println("getRescaledInstance: imageCache.getImageById = NULL for size: " +  size + ", id: " + id);
		}else{

			if(imageCache.nativeImageExists(id))
			{
				System.err.println("getRescaledInstance: creating thumb from existing NATIVE thumb.");

				sourceImage = imageCache.getNativeImage(id);
				if(sourceImage == null)
					System.err.println("getRescaledInstance: imageCache.getNativeImage = NULL for id: " +  id);
			}
		}

		if(sourceImage != null){
			System.out.println("getRescaledInstance: rescaling source image to thumb. id: --> " + id);

			if(sourceImage instanceof AsyncImage<?>){
				sourceImage = ((AsyncImage) sourceImage).waitForImage();
				if(sourceImage == null) System.err.println("getRescaledInstance:  AsyncImage.waitForImage() failed. Image = NULL");
			}
			if(sourceImage != null)
				resizedImage =  ScalrEx.rescaleImage(sourceImage, desiredSize.width, desiredSize.height);
		}

		return resizedImage;
	}

}
