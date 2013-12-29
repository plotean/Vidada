package vidada.views.mediabrowsers.mediaFileBrowser;

import archimedesJ.io.locations.DirectoryLocation;
import archimedesJ.swing.components.thumbexplorer.model.IThumbNodeFactory;
import archimedesJ.swing.components.thumbexplorer.model.locations.LocationBaseNode;
import archimedesJ.swing.components.thumbexplorer.model.locations.ThumbDirectoryNode;
import archimedesJ.swing.components.thumbpresenter.model.IMediaDataProvider;

public class VidadaFolderTreeNodeWrapper extends ThumbDirectoryNode {

	public VidadaFolderTreeNodeWrapper(LocationBaseNode parent, DirectoryLocation folderLocation,
			IThumbNodeFactory thumbNodeFactory) {
		super(parent, folderLocation, thumbNodeFactory);
	}


	@Override
	protected IMediaDataProvider loadMediaDataProvider() {

		IMediaDataProvider mediaItem = null;

		if(mediaItem == null){
			mediaItem = new VidadaImageMediaDataProvider(this);
			System.out.println("IMediaDataProvider in VidadaFolderTreeNodeWrapper has been loaded");
		}


		return mediaItem;
	}





}
