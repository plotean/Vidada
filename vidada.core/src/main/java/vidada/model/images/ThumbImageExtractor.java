package vidada.model.images;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;
import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.ImageMediaItem;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.MovieMediaItem;
import vidada.model.media.source.MediaSource;
import vidada.model.video.Video;
import vidada.services.ServiceProvider;

import java.io.IOException;
import java.io.InputStream;

public class ThumbImageExtractor implements IThumbImageCreator {

	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/


	/**{@inheritDoc}*/
	@Override
	public boolean canExtractThumb(MediaItem media){
		if(media.getType() == MediaType.IMAGE)
			return true;
		if(media.getType() == MediaType.MOVIE){
			return ((MovieMediaItem) media).canCreateThumbnail() && Video.isGenericEncoderPresent();
		}else{
			System.err.println("ThumbImageExtractor::canExtractThumb: Unknown media type: " + media.getClass().getName());
			return false;
		}
	}


	/**{@inheritDoc}*/
	@Override
	public IMemoryImage extractThumb(MediaItem media, Size size){
		IMemoryImage image = null;

		if(media.getType() == MediaType.IMAGE){
			image = extractImageThumb((ImageMediaItem)media, size);
		}else if(media.getType() == MediaType.MOVIE){
			image = extractMovieThumb((MovieMediaItem)media, size);
		}else
			throw new NotSupportedException("Unknown media type: " + media.getClass().getName());

		return image;
	}



	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Read the image file into a IMemoryImage in its native resolution
	 */
	private IMemoryImage readNativeImage(ImageMediaItem media) {
		IMemoryImage bufferedImage = null;

		MediaSource source = media.getSource();

		if(source != null){
			ResourceLocation filePath = source.getResourceLocation();
			if (filePath != null && filePath.exists()) {
				System.out.println("reading image...");
				InputStream is = null;
				try {
					is = filePath.openInputStream();
					bufferedImage = imageFactory.createImage(is);
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

	private IMemoryImage extractImageThumb(ImageMediaItem media, Size size){
		IMemoryImage nativeImage = readNativeImage(media);
		if(nativeImage != null)
			nativeImage = nativeImage.rescale(size.width, size.height);
		return nativeImage;
	}

	private IMemoryImage extractMovieThumb(MovieMediaItem media, Size size){
		return extractMovieThumb(media, size, media.getThumbPos());
	}

	/**
	 * Create a thumb at the given position.
	 * 
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	private IMemoryImage extractMovieThumb(MovieMediaItem media, Size size, float position) {

		Video video = getVideo(media);

		IMemoryImage frame = video.getFrame(position, size);
		if (frame != null) {
			media.setCurrentThumbPosition(position);
			media.onThumbCreationSuccess();
		} else {
			// the thumb could not be generated
			media.onThumbCreationFailed();
		}

		return frame;
	}

	private Video getVideo(MovieMediaItem media){
		Video video = null;
		MediaSource source = media.getSource();
		if(source != null && source.isAvailable())
		{
			ResourceLocation path = source.getResourceLocation();
			video = new Video(path);
		}
		return video;
	}

}
