package vidada.model.media.images;

import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import vidada.model.cache.CacheUtils;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.settings.GlobalSettings;
import archimedesJ.data.hashing.FileHashAlgorythms;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.geometry.Size;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.TaskResult;
import archimedesJ.util.images.ImageInfo;
import archimedesJ.util.images.ScalrEx;
import archimedesJ.util.images.SimpleImageInfo;

public class ImageMediaItem extends MediaItem {

	/**
	 * Empty hibernate constructor
	 */
	public ImageMediaItem() {
	}

	/**
	 * Creates a copy of the given prototype
	 * @param prototype
	 */
	public ImageMediaItem(ImageMediaItem prototype){
		prototype(prototype);
	}

	/**
	 * Create a new Imagepart
	 * 
	 * @param parentLibrary
	 * @param relativeFilePath
	 * @param hash
	 */
	public ImageMediaItem(MediaLibrary parentLibrary, URI relativeFilePath, String hash) {
		super(parentLibrary, relativeFilePath);
		setFilehash(hash);
		setMediaType(MediaType.IMAGE);
	}

	@Override
	public TaskResult createCachedThumbnail(Size size) {

		TaskResult state;

		BufferedImage thumb;
		Size maxThumb = GlobalSettings.getMaxThumbResolution();

		if(!imageCache.exists(getThumbId(), maxThumb))
		{
			// Ensure that the biggest thumb is created.
			// Other thumbs can be created based on this one 
			// which makes thumb creation for big images
			// much faster.

			BufferedImage frame = extractNativeSizeFrame();
			if(frame != null){
				thumb =  ScalrEx.rescaleImage(frame, maxThumb.width, maxThumb.height);
				imageCache.storeImage(getFilehash(), thumb);
			}
		}

		thumb = CacheUtils.getRescaledInstance(imageCache, getThumbId(), size);

		if(thumb != null){
			imageCache.storeImage(getFilehash(), thumb);
			state = TaskResult.Completed;
		}else
			state = TaskResult.Failed;

		return state;
	}

	@Override
	public boolean preloadThumbnail(Size size) {
		return !imageCache.exists(getThumbId(), size);
	}

	@Override
	@Transient
	public void resolveResolution(){
		try {

			MediaSource source = getSource();
			if(source instanceof FileMediaSource)
			{
				FileMediaSource fileSource = (FileMediaSource)source;

				ResourceLocation imagePath = fileSource.getAbsoluteFilePath();
				if(imagePath.exists()){

					InputStream is = null;
					try{
						is = imagePath.openInputStream();
						ImageInfo info = SimpleImageInfo.parseInfo(is);
						if(info != null && info.isValid())
							setResolution(new Size(info.getWidth(), info.getHeight()));
						else
							System.err.println("resolveResolution(): ImageInfo is NOT VALID! " + info);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if(is != null){
							is.close();
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * On plain images, this will just read the image
	 */
	protected BufferedImage extractNativeSizeFrame() {
		BufferedImage bufferedImage = null;


		MediaSource source = getSource();
		if(source instanceof FileMediaSource)
		{
			FileMediaSource fileSource = (FileMediaSource)source;

			ResourceLocation filePath = fileSource.getAbsoluteFilePath();
			if (filePath.exists()) {
				System.out.println("reading image...");
				InputStream is = null;
				try {
					is = filePath.openInputStream();
					bufferedImage = ImageIO.read(is);
					if(!hasResolution())
						setResolution(new Size(bufferedImage.getWidth(), bufferedImage.getHeight()));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Can not read image" + filePath.toString());
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
		}

		return bufferedImage;
	}

	@Transient
	public static IFileHashAlgorythm getHashStrategy() {
		return FileHashAlgorythms.instance().getButtikscheHashAlgorythm();
	}

	@Override
	public boolean canCreateThumbnail() {
		return true;
	}
}
