package vidada.views.mediabrowsers.decorators.overlays;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.TreeNode;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.viewer.IImageProvider;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.images.viewer.SmartImageWrapper;
import archimedesJ.swing.components.thumbexplorer.model.files.FileTreeNode;
import archimedesJ.swing.components.thumbexplorer.model.files.ThumbFolderNode;
import archimedesJ.swing.components.thumbpresenter.model.IMediaDataProvider;
import archimedesJ.util.OSValidator;

public class FolderNodeImageProvider implements IImageProvider {

	private final EventHandlerEx<EventArgs> CurrentImageChanged = new EventHandlerEx<EventArgs>();
	private final Dimension itemSize;
	List<IMediaDataProvider> children;

	public FolderNodeImageProvider(ThumbFolderNode folderNode, Dimension itemSize) {

		this.itemSize = itemSize;

		children = new ArrayList<IMediaDataProvider>();

		for (TreeNode child : Collections.list(folderNode.children())) {

			if (child instanceof FileTreeNode) {
				IMediaDataProvider fileNode = (IMediaDataProvider) child;
				if (fileNode.canCreateThumbnail()) {
					children.add((IMediaDataProvider) fileNode);
				}
			}
		}
		System.out.println("Possible children found: " + children.size());

		// Set the first image
		if (!children.isEmpty()) {
			setMediaDataProvider(children.get(0));
		}

	}

	@Override
	public IEvent<EventArgs> getCurrentImageChanged() {
		return CurrentImageChanged;
	}

	/**
	 * Set the image n of the current m images.
	 * 
	 * 0.0  => first image
	 * 0.5  => m/2
	 * 1.0 	=> last image
	 * @param position
	 *            0.0 - 1.0
	 */

	public void setCurrentImageRelative(float position) {
		if(!children.isEmpty())
		{
			int index = (int) ((float) children.size() * position);
			setMediaDataProvider(children.get(index));
		}
	}

	@SuppressWarnings("rawtypes")
	private void setMediaDataProvider(IMediaDataProvider media) {

		ImageContainer container = media.getThumbnail(getImageResolution());
		container.awaitImage();

		IMemoryImage image = container.getRawImage();

		if (image != null) {
			SmartImageWrapper imageWrapper = new SmartImageWrapper(image, media.getTitle());
			setCurrentImage(imageWrapper);
		} else {
			System.err.println("setCurrentImageRelative: no thumb found (NULL)");
		}
	}

	@Override
	public void navigateNext() {
		// ignored because handling happens over setCurrentImageRelative
	}

	@Override
	public void navigatePrevious() {
		// ignored because handling happens over setCurrentImageRelative
	}

	private ISmartImage currentImage;

	@Override
	public ISmartImage currentImage() {
		return currentImage;
	}

	public void setCurrentImage(ISmartImage image) {
		this.currentImage = image;
		CurrentImageChanged.fireEvent(this, EventArgs.Empty);
	}

	private Size getImageResolution() {
		Size resolution;

		if (OSValidator.isHDPI()) {
			int width = (int) (itemSize.getWidth() * 2);
			int height = (int) (itemSize.getHeight() * 2);
			resolution = new Size(width,height);
		}else{
			resolution = Size.Empty;
		}

		return resolution;

	}

}
