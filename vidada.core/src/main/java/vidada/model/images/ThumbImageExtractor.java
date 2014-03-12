package vidada.model.images;

import java.io.IOException;
import java.io.InputStream;

import vidada.model.media.ImageMediaItem;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.MovieMediaItem;
import vidada.model.media.source.MediaSource;
import vidada.model.video.Video;
import vidada.model.video.VideoInfo;
import vidada.services.ServiceProvider;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.images.ImageInfo;
import archimedesJ.swing.images.SimpleImageInfo;

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


	/**{@inheritDoc}*/
	@Override
	public boolean updateInfo(MediaItem media) {
		if(media.getType().equals(MediaType.IMAGE)){
			return resolveImageResolution((ImageMediaItem)media);
		}else if(media.getType().equals(MediaType.MOVIE)){
			return resolveMovieResolution((MovieMediaItem)media);
		}else{
			throw new NotSupportedException("Not supported media type: " + media.getType());
		}
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
			updateInfo(media);
			media.setCurrentThumbPosition(position);
			media.onThumbCreationSuccess();
		} else {
			// the thumb could not be generated
			media.onThumbCreationFailed();
		}

		return frame;
	}

	private boolean resolveMovieResolution(MovieMediaItem movieMedia){
		boolean success = false;
		Video myVideo = getVideo(movieMedia);
		if(myVideo != null){
			VideoInfo info = myVideo.getVideoInfo();
			if(info != null){
				movieMedia.setResolution(info.NativeResolution);
				success = true;
			}
		}
		return success;
	}

	private boolean resolveImageResolution(ImageMediaItem imageMedia){
		boolean success = false;

		try {
			MediaSource source = imageMedia.getSource();

			ResourceLocation imagePath = source.getResourceLocation();
			if(imagePath != null && imagePath.exists()){

				InputStream is = null;
				try{
					is = imagePath.openInputStream();
					ImageInfo info = SimpleImageInfo.parseInfo(is);
					if(info != null && info.isValid()){
						imageMedia.setResolution(new Size(info.getWidth(), info.getHeight()));
						success = true;
					}else
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

		return success;
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
