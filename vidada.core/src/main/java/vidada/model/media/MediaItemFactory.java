package vidada.model.media;

import archimedesJ.io.locations.ResourceLocation;

public class MediaItemFactory {

	private final static MediaItemFactory instance = new MediaItemFactory();

	public synchronized static MediaItemFactory instance(){
		return instance;
	}


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
			mediahash =  MediaHashUtil.getDefaultMediaHashUtil().retriveFileHash(mediaLocation);
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
			System.err.println("MediaService: Can not handle " + mediaLocation.toString());
		}

		return newMedia;
	}

}
