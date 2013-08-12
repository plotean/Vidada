package vidada.views.mediabrowsers.mediaFileBrowser;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.components.thumbexplorer.model.locations.LocationBaseNode;
import archimedesJ.swing.components.thumbexplorer.model.locations.ThumbLocationNode;
import archimedesJ.swing.components.thumbpresenter.model.IMediaDataProvider;
import archimedesJ.swing.components.thumbpresenter.model.ImageMediaDataProvider;


/**
 * Maps a common file to a IMediaDataProvider
 * @author IsNull
 *
 */
public class VidadaFileTreeNodeWrapper extends ThumbLocationNode implements IHaveMediaData {

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	public VidadaFileTreeNodeWrapper(LocationBaseNode parent, ResourceLocation location) {
		super(parent, location);
	}

	@Override
	protected IMediaDataProvider loadMediaDataProvider() {

		IMediaDataProvider mediaItem = getMediaData();

		if(mediaItem == null)
			mediaItem = new ImageMediaDataProvider(getName(), null);

		return mediaItem;
	}


	private MediaItem mediaData = null;

	/**
	 * Returns the MediaData for this file or null if not available
	 * @return
	 */
	@Override
	public MediaItem getMediaData(){
		if(mediaData == null){
			mediaData = mediaService.findMediaData(getLocation());
		}
		return mediaData; 
	}
}
