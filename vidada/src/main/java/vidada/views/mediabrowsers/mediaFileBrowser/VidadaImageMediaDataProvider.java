package vidada.views.mediabrowsers.mediaFileBrowser;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import vidada.model.settings.GlobalSettings;
import archimedesJ.data.ObservableBean;
import archimedesJ.expressions.Predicate;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.StaticImageContainer;
import archimedesJ.swing.components.thumbexplorer.model.locations.ThumbDirectoryNode;
import archimedesJ.swing.components.thumbexplorer.model.locations.ThumbLocationNode;
import archimedesJ.swing.components.thumbpresenter.model.IMediaDataProvider;
import archimedesJ.swing.images.MemoryImageAwt;
import archimedesJ.threading.TaskResult;
import archimedesJ.util.Lists;
import archimedesJ.util.OSValidator;

public class VidadaImageMediaDataProvider extends ObservableBean implements IMediaDataProvider {

	private final ThumbDirectoryNode folderNode;
	private Map<Size, ImageContainer> imageMap;


	public VidadaImageMediaDataProvider(ThumbDirectoryNode folderNode) {
		this.folderNode = folderNode;
		imageMap = new HashMap<Size, ImageContainer>();
	}

	List<Image> goodImages = null;

	/**
	 * Get the images
	 * 
	 * @param amount
	 * @return
	 */
	private List<Image> getImages(int amount) {

		if (goodImages != null)
			return goodImages;

		List<Image> images = null;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ThumbLocationNode> leafChildren = (List) Lists.where(
				Collections.list(this.folderNode.children()), new Predicate<TreeNode>() {
					@Override
					public boolean where(TreeNode node) {
						if (node instanceof IMediaDataProvider) {
							IMediaDataProvider fileNode = (IMediaDataProvider) node;
							return fileNode.canCreateThumbnail();
						}
						return false;
					}
				});

		int possibleImages = Math.min(amount, leafChildren.size());
		amount = Math.min(amount, leafChildren.size());

		if (leafChildren.size() == 0) {
			Image image = processSubFolder(this.folderNode);
			if (image != null) {
				images = new ArrayList<Image>();
				images.add(image);
				goodImages = images;
			}
		} else {
			images = processRootImages(leafChildren, amount);
			if (images != null) {
				if (images.size() != possibleImages)
					images.clear();
				else
					goodImages = images;
			}
		}

		return images;
	}

	private Image processSubFolder(ThumbDirectoryNode node) {
		Image img = null;
		List<TreeNode> children = Collections.list(node.children());
		for (TreeNode n : children) {
			if (n instanceof IMediaDataProvider) {
				img = retriveImageFromNode((IMediaDataProvider) n);
				if (img != null)
					break;
			}
		}
		return img;
	}

	private List<Image> processRootImages(List<ThumbLocationNode> children, int amount) {
		List<Image> images = null;

		if (amount > 0) {
			images = new ArrayList<Image>();

			for (int i = 0; i < amount; i++) {
				int index = (int) (Math.random() * (children.size() - 1));
				IMediaDataProvider node = children.get(index);
				Image image = retriveImageFromNode(node);
				if (image != null)
					images.add(image);
				children.remove(index);
			}
		}
		return images;
	}

	/**
	 * Retrives an image from the given IMediaDataProvider
	 * 
	 * @param thumbNode
	 * @return
	 */
	private Image retriveImageFromNode(IMediaDataProvider thumbNode) {

		Size size = getImageResolution();

		ImageContainer thumb = thumbNode.getThumbnail(size);
		thumb.awaitImage();

		return (Image)thumb.getRawImage();
	}

	/**
	 * Returns the required image resolution (depends on the DPI)
	 * 
	 * @return
	 */
	private Size getImageResolution() {
		Size size = new Size(GlobalSettings.THUMBNAIL_SIZE_MAX, (int) (GlobalSettings.THUMBNAIL_SIZE_MAX * GlobalSettings.THUMBNAIL_SIDE_RATIO));

		if (OSValidator.isHDPI()) {
			size.width *= 2;
			size.height *= 2;
		}

		return size;
	}


	private int getImageAmount() {
		int amount = 0;

		List<TreeNode> children = Collections.list(this.folderNode.children());

		if (children != null) {
			if (children.size() < 4) {
				amount = children.size();
			} else {
				amount = 4;
			}
		}
		return amount;
	}

	private boolean canCreateFolderThumb = true;

	@Override
	public synchronized void createThumbnailCached(Size size) {
		TaskResult state = TaskResult.Failed;
		iscreatingThumbnail = true;

		try {

			List<Image> images = getImages(getImageAmount());

			if (images == null) {
				System.err.println("Failed to create folder thumb! " + getTitle());
				canCreateFolderThumb = false;
				//return TaskResult.Failed;
			}

			if (!images.isEmpty()) {

				FolderThumbImageComposer imageComposer = new FolderThumbImageComposer();
				BufferedImage image = imageComposer.createImageComposition(images, size, 0.11f, 20);

				imageMap.put(size, new StaticImageContainer(new MemoryImageAwt(image)));
				state = TaskResult.Completed;
				firePropertyChange("cachedThumbnail");
				System.out.println("miniThumb Image has been added to cachedThumbnail");
			} else {
				state = TaskResult.Skipped;
				System.err.println("Waiting for images - skipped this thumb! " + getTitle());
			}
		} finally {
			iscreatingThumbnail = false;
		}
		//return state;
	}


	@Override
	public ImageContainer getThumbnail(Size size) {
		return imageMap.get(size);
	}

	private volatile boolean iscreatingThumbnail = false;

	@Override
	public boolean canCreateThumbnail() {
		return canCreateFolderThumb;
	}

	@Override
	public String getTitle() {
		return this.folderNode.getName();
	}

	@Override
	public String getDescription() {
		return "";
	}



}
