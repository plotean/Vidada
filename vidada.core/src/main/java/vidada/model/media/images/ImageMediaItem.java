package vidada.model.media.images;

import java.beans.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.store.libraries.MediaLibrary;
import archimedesJ.data.hashing.FileHashAlgorythms;
import archimedesJ.data.hashing.IFileHashAlgorythm;
import archimedesJ.geometry.Size;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.images.ImageInfo;
import archimedesJ.swing.images.SimpleImageInfo;

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
	@Transient
	public void resolveResolution(){
		try {

			IMediaSource source = getSource();

			ResourceLocation imagePath = source.getResourceLocation();
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


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Transient
	public static IFileHashAlgorythm getHashStrategy() {
		return FileHashAlgorythms.instance().getButtikscheHashAlgorythm();
	}
}
