package vidada.model.images;

import java.io.IOException;
import java.io.InputStream;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.media.source.IMediaSource;
import vidada.model.video.Video;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.io.locations.ResourceLocation;

public class ThumbImageExtractor implements IThumbImageCreator {

	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);


	/* (non-Javadoc)
	 * @see vidada.model.images.IThumbImageCreator#canExtractThumb(vidada.model.media.MediaItem)
	 */
	@Override
	public boolean canExtractThumb(MediaItem media){
		if(media instanceof ImageMediaItem){
			return true;
		}else if(media instanceof MovieMediaItem){
			return ((MovieMediaItem) media).canCreateThumbnail() && Video.isGenericEncoderPresent();
		}else{
			System.err.println("ThumbImageExtractor::canExtractThumb: Unknown media type: " + media.getClass().getName());
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see vidada.model.images.IThumbImageCreator#extractThumb(vidada.model.media.MediaItem, archimedesJ.geometry.Size)
	 */
	@Override
	public IMemoryImage extractThumb(MediaItem media, Size size){
		IMemoryImage image = null;

		if(media instanceof ImageMediaItem){
			image = extractImageThumb((ImageMediaItem)media, size);
		}else if(media instanceof MovieMediaItem){
			image = extractMovieThumb((MovieMediaItem)media, size);
		}else
			throw new NotSupportedException("Unknown media type: " + media.getClass().getName());

		return image;
	}

	/**
	 * Read the image file into a IMemoryImage in its native resolution
	 */
	public IMemoryImage readImage(ImageMediaItem media) {
		IMemoryImage bufferedImage = null;


		IMediaSource source = media.getSource();

		if(source != null){
			ResourceLocation filePath = source.getResourceLocation();
			if (filePath != null && filePath.exists()) {
				System.out.println("reading image...");
				InputStream is = null;
				try {
					is = filePath.openInputStream();
					bufferedImage = imageFactory.createImage(is);
					//if(!hasResolution())
					//	setResolution(new Size(bufferedImage.getWidth(), bufferedImage.getHeight()));
					//bufferedImage = bufferedImage.rescale(size.width, size.height);
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

	public IMemoryImage extractImageThumb(ImageMediaItem media, Size size){
		IMemoryImage nativeImage = readImage(media);
		// TODO how to update media resolution here?
		nativeImage = nativeImage.rescale(size.width, size.height);
		return nativeImage;
	}

	public IMemoryImage extractMovieThumb(MovieMediaItem media, Size size){
		IMemoryImage frame = null;

		float pos;
		// TODO Maybe move to media?
		if (media.getPreferredThumbPosition() != MovieMediaItem.INVALID_POSITION) {
			pos = media.getPreferredThumbPosition();
		} else if (media.getCurrentThumbPosition() != MovieMediaItem.INVALID_POSITION) {
			pos = media.getCurrentThumbPosition();
		} else {
			pos = (float)Math.random();
		}

		frame = extractMovieThumb(media, size, pos);
		return frame;
	}

	/**
	 * Create a thumb at the given position.
	 * 
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	public IMemoryImage extractMovieThumb(MovieMediaItem media, Size size, float position) {

		Video video = media.getVideo();

		IMemoryImage frame = video.getFrame(position, size);
		if (frame != null) {
			media.resolveResolution();
			media.setCurrentThumbPosition(position);
			media.onThumbCreationSuccess();
		}else {
			// the thumb could not be generated
			media.onThumbCreationFailed();
		}

		return frame;
	}

}
