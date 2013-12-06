package vidada.views.mediabrowsers.imageviewer;

import java.io.IOException;
import java.io.InputStream;

import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.source.MediaSource;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Wraps a SmartImage to a ImageMediaItem
 * 
 * @author IsNull
 *
 */
public class SmartImageMediaItemAdapter implements ISmartImage {

	private final ImageMediaItem imageMedia;
	private final IRawImageFactory imageFactory;

	private IMemoryImage image;

	public SmartImageMediaItemAdapter(IRawImageFactory imageFactory, ImageMediaItem imageMedia){
		this.imageFactory = imageFactory;
		this.imageMedia = imageMedia;
	}

	@Override
	public String getTitle() {
		return imageMedia.getTitle();
	}

	@Override
	public IMemoryImage getImage() {

		if(imageMedia != null && image == null){
			MediaSource source = imageMedia.getSource();
			if(source.isAvailable())
			{
				ResourceLocation resource = source.getResourceLocation();
				if(resource != null) {
					InputStream is = null;
					try {
						is = resource.openInputStream();
						image = imageFactory.createImage(is);
					}finally{
						if(is != null){
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}else
				System.err.println("ImagePartWrapper.getImage: image source invalid: " + source);
		}

		return image;
	}

}
