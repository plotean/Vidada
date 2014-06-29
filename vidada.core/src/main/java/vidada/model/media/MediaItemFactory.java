package vidada.model.media;

import archimedes.core.io.locations.ResourceLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class MediaItemFactory {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/


    private static final Logger logger = LogManager.getLogger(MediaItemFactory.class.getName());

    private final static MediaItemFactory instance = new MediaItemFactory();


    /***************************************************************************
     *                                                                         *
     * Singleton                                                               *
     *                                                                         *
     **************************************************************************/

    public synchronized static MediaItemFactory instance(){
        return instance;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 * Simple media factory to create a media item from an existing file.
	 * The media type is determined dynamically. 
	 * @param mediaLocation
	 * @param parentlibrary
	 * @return
	 */
	public MediaItem buildMedia(final ResourceLocation mediaLocation, final MediaLibrary parentlibrary){
		return buildMedia(mediaLocation, parentlibrary, null);
	}

	/**
	 * Simple media factory to create a media item from an existing file.
	 * The media type is determined dynamically. 
	 * @param mediaLocation
	 * @param parentlibrary
	 * @param mediahash
	 * @return
	 */
	public MediaItem buildMedia(final ResourceLocation mediaLocation, final MediaLibrary parentlibrary, String mediahash) {

		MediaItem newMedia = null;

		if(mediahash == null){
			// if no hash has been provided we have to calculate it now
			mediahash =  MediaHashUtil.getDefaultMediaHashUtil().retrieveFileHash(mediaLocation);
		}

		// find the correct Media type for the given media
		if(MediaFileInfo.get(MediaType.MOVIE).isFileofThisType(mediaLocation))
		{
			newMedia = new MovieMediaItem(
					parentlibrary,
					parentlibrary.getMediaDirectory().getRelativePath(mediaLocation),
					mediahash);

		}else if(MediaFileInfo.get(MediaType.IMAGE).isFileofThisType(mediaLocation)){

			newMedia = new ImageMediaItem(
					parentlibrary,
					parentlibrary.getMediaDirectory().getRelativePath(mediaLocation),
					mediahash);

		}else {
            logger.error("Can not build media for " + mediaLocation.toString());
		}

        Path mediaFile = new File(mediaLocation.getPath()).toPath();
        try {
            BasicFileAttributes attr = Files.readAttributes(mediaFile, BasicFileAttributes.class);
            FileTime time = attr.creationTime();
            newMedia.setAddedDate(new DateTime(time.toMillis()));
        } catch (IOException e) {
            logger.error(e);
        }

        return newMedia;
	}

}
