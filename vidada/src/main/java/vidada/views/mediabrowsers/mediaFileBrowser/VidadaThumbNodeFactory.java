package vidada.views.mediabrowsers.mediaFileBrowser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.swing.components.thumbexplorer.IBaseTreeItem;
import archimedesJ.swing.components.thumbexplorer.model.IThumbNodeFactory;
import archimedesJ.swing.components.thumbexplorer.model.locations.ThumbDirectoryNode;

/**
 * Node factory for vidada file explorer
 * 
 * @author IsNull
 * 
 */
public class VidadaThumbNodeFactory implements IThumbNodeFactory {

	@Override
	public List<TreeNode> loadChildren(IBaseTreeItem parent) {

		List<TreeNode> nodes = new ArrayList<TreeNode>();


		if(parent instanceof ThumbDirectoryNode)
		{
			ThumbDirectoryNode parentNode = (ThumbDirectoryNode) parent;
			DirectoiryLocation directory = parentNode.getLocation();

			List<DirectoiryLocation> dirs = directory.listDirs();
			List<ResourceLocation> resurces = directory.listFiles();

			for (DirectoiryLocation dir : dirs) {
				nodes.add(createNodeForFolder(parentNode, dir));
			}

			for (ResourceLocation res : resurces) {
				nodes.add(createNodeForFile(parentNode, res));
			}
		}

		return nodes;
	}



	protected TreeNode createNodeForFile(ThumbDirectoryNode parent, ResourceLocation file) {
		return new VidadaFileTreeNodeWrapper(parent, file);
	}

	protected TreeNode createNodeForFolder(ThumbDirectoryNode parent, DirectoiryLocation folder) {
		return new VidadaFolderTreeNodeWrapper(parent, folder, this);
	}



}
