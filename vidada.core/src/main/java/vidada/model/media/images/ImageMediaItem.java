package vidada.model.media.images;

import java.beans.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import vidada.model.ServiceProvider;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import archimedesJ.data.hashing.FileHashAlgorythms;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.images.ImageInfo;
import archimedesJ.swing.images.SimpleImageInfo;

public class ImageMediaItem extends MediaItem {

	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

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
	public void createThumbnailCached(Size size) {

		//TaskResult state;

		IMemoryImage frame = readImage(size);
		if(frame != null)
			imageService.storeImage(this, frame);
		else {
			System.err.println("Reading image failed: " + this);
		}
		//return state;
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
				if(imagePath != null && imagePath.exists()){

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
	private IMemoryImage readImage(Size size) {
		IMemoryImage bufferedImage = null;


		MediaSource source = getSource();
		if(source instanceof FileMediaSource)
		{
			FileMediaSource fileSource = (FileMediaSource)source;

			ResourceLocation filePath = fileSource.getAbsoluteFilePath();
			if (filePath != null && filePath.exists()) {
				System.out.println("reading image...");
				InputStream is = null;
				try {
					is = filePath.openInputStream();
					bufferedImage = imageFactory.createImage(is);
					if(!hasResolution())
						setResolution(new Size(bufferedImage.getWidth(), bufferedImage.getHeight()));

					bufferedImage = bufferedImage.rescale(size.width, size.height);

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
