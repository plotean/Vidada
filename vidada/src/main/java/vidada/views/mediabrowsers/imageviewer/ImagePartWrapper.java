package vidada.views.mediabrowsers.imageviewer;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.components.imageviewer.ISmartImage;

/**
 * 
 * @author IsNull
 *
 */
public class ImagePartWrapper implements ISmartImage {

	private ImageMediaItem imageMedia;
	private Image image;

	public ImagePartWrapper(ImageMediaItem imageMedia){
		this.imageMedia = imageMedia;
	}

	@Override
	public String getTitle() {
		return imageMedia.getTitle();
	}

	@Override
	public Image getImage() {

		if(imageMedia != null && image == null){
			MediaSource source = imageMedia.getSource();
			if(source instanceof FileMediaSource && source.isAvailable())
			{
				ResourceLocation resource = ((FileMediaSource)source).getAbsoluteFilePath();
				InputStream is = null;
				try {
					is = resource.openInputStream();
					image = ImageIO.read(is);

				} catch (IOException e) {
					System.err.println("can't read " + resource);
					e.printStackTrace();
				}finally{
					if(is != null){
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}else
				System.err.println("ImagePartWrapper.getImage: image source invalid: " + source);
		}

		return image;
	}

}
