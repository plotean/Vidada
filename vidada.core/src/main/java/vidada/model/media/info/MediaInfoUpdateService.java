package vidada.model.media.info;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.swing.images.ImageInfo;
import archimedes.core.swing.images.SimpleImageInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.media.*;
import vidada.model.media.extracted.IMediaPropertyStore;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.video.Video;
import vidada.model.video.VideoInfo;

import java.io.IOException;
import java.io.InputStream;

public class MediaInfoUpdateService implements IMediaInfoUpdateService {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaInfoUpdateService.class.getName());


    private static String PROPERTY_RESOLUTION = "resolution";
	private static String PROPERTY_DURATION = "duration";
	private static String PROPERTY_BITRATE = "bitrate";


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**{@inheritDoc}*/
	@Override
	public boolean updateInfo(MediaItem media){
		if(media.getType().equals(MediaType.IMAGE)){
			return updateImageInfo((ImageMediaItem)media);
		}else if(media.getType().equals(MediaType.MOVIE)){
			return updateMovieInfo((MovieMediaItem)media);
		}else{
			throw new NotSupportedException("Not supported media type: " + media.getType());
		}
	}

    /**{@inheritDoc}*/
	@Override
	public boolean updateInfoFromCache(MediaItem media, IMediaPropertyStore propertyStore){
		if(media.getType().equals(MediaType.IMAGE)){
			return updateImageInfoCache((ImageMediaItem)media, propertyStore);
		}else if(media.getType().equals(MediaType.MOVIE)){
			return updateMovieInfoCache((MovieMediaItem)media, propertyStore);
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
     * Updates the given movieMedia's properties from the cached values where necessary
     * @param movieMedia
     * @param propertyStore
     * @return
     */
	private boolean updateMovieInfoCache(MovieMediaItem movieMedia, IMediaPropertyStore propertyStore) {
		boolean updatedMedia = false;

		if(!movieMedia.hasResolution()){
			// PROPERTY_RESOLUTION
			Size resolution = Size.parse(propertyStore.getProperty(movieMedia, PROPERTY_RESOLUTION));
			if(resolution != null && !resolution.isEmpty()){
				movieMedia.setResolution(resolution);
				updatedMedia = true;
			}

			// PROPERTY_BITRATE
			int bitrate = parseIntRelaxed(propertyStore.getProperty(movieMedia, PROPERTY_BITRATE));
			if(bitrate != 0){
				movieMedia.setBitrate(bitrate);
				updatedMedia = true;
			}

			// PROPERTY_DURATION
			int duration = parseIntRelaxed(propertyStore.getProperty(movieMedia, PROPERTY_DURATION));
			if(duration != 0){
				movieMedia.setDuration(duration);
				updatedMedia = true;
			}
		}
		return updatedMedia;
	}



	private boolean updateMovieInfo(MovieMediaItem movieMedia){

		boolean updatedMedia = false;

		IMediaPropertyStore propertyStore = getLibrary(movieMedia).getPropertyStore();

		if(!movieMedia.hasResolution()){ // All the other properties than resolution are not guaranteed to be extracted, so don't stress here
			if(!(updatedMedia = updateMovieInfoCache(movieMedia, propertyStore))){
				updatedMedia = extractMovieInfo(movieMedia, propertyStore);
			}
		}

		return updatedMedia;
	}

	private boolean extractMovieInfo(MovieMediaItem movieMedia, IMediaPropertyStore propertyStore){
		boolean success = false;
		Video myVideo = getVideo(movieMedia);
		if(myVideo != null){
			VideoInfo info = myVideo.getVideoInfo();
			if(info != null){
				movieMedia.setResolution(info.NativeResolution);
				movieMedia.setBitrate(info.BitRate);
				propertyStore.setProperty(movieMedia, PROPERTY_RESOLUTION, info.NativeResolution.toString());
				propertyStore.setProperty(movieMedia, PROPERTY_BITRATE, info.BitRate+"");
				propertyStore.setProperty(movieMedia, PROPERTY_DURATION, info.Duration+"");
				success = true;
			}
		}
		return success;
	}

	private boolean updateImageInfo(ImageMediaItem imageMedia){
		boolean updatedMedia = false;
		IMediaPropertyStore propertyStore = getLibrary(imageMedia).getPropertyStore();

		if(!(updatedMedia = updateImageInfoCache(imageMedia, propertyStore))){
			updatedMedia = extractImageInfo(imageMedia, propertyStore);
		}
		return updatedMedia;
	}

	private boolean updateImageInfoCache(ImageMediaItem imageMedia, IMediaPropertyStore propertyStore) {
		boolean updatedMedia = false;

		if(!imageMedia.hasResolution()){
			Size resolution = Size.parse(propertyStore.getProperty(imageMedia, PROPERTY_RESOLUTION));
			if(resolution != null && !resolution.isEmpty()){
				imageMedia.setResolution(resolution);
				updatedMedia = true;
			}
		}

		return updatedMedia;
	}

	private boolean extractImageInfo(ImageMediaItem imageMedia, IMediaPropertyStore propertyStore){
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
						Size resolution = new Size(info.getWidth(), info.getHeight());
						propertyStore.setProperty(imageMedia, PROPERTY_RESOLUTION, resolution.toString());
						imageMedia.setResolution(resolution);
						success = true;
					}else
                        logger.error("resolveResolution(): ImageInfo is NOT VALID! " + info);
				}catch(Exception e){
                    logger.error(e);
				}finally{
					if(is != null){
						is.close();
					}
				}
			}
		} catch (IOException e) {
            logger.error(e);
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

	private MediaLibrary getLibrary(MediaItem media){
		MediaLibrary library = null;
		MediaSource source = media.getSource();
		if(source != null && source.isAvailable())
		{
			library = ((MediaSourceLocal) source).getParentLibrary();
		}
		return library;
	}

	private int parseIntRelaxed(String str){
		int i = 0;
		try{
			if(str != null && !str.isEmpty()){
				i = Integer.parseInt(str);
			}
		}catch(NumberFormatException e){
			i = 0;
		}
		return i;
	}


}
