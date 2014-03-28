package vidada.viewsFX.mediaexplorer;

import javafx.scene.control.TreeItem;

import org.controlsfx.control.BreadCrumbBar;

import archimedes.core.io.locations.DirectoryLocation;

public class BreadCrumbNavigationDecorator {

	private final HomeLocationBreadCrumb home;
	private final BreadCrumbBar<LocationBreadCrumb> breadCrumbBar;
	private DirectoryLocation root;
	private DirectoryLocation directory;

	public BreadCrumbNavigationDecorator(BreadCrumbBar<LocationBreadCrumb> breadCrumbBar, HomeLocationBreadCrumb home){
		this.home = home;
		this.breadCrumbBar = breadCrumbBar;

		updateModel();
	}

	public void setDirectory(DirectoryLocation home, DirectoryLocation directory){

		System.out.println("LocationBreadCrumbBarModel: " + directory);

		if(this.root == home && this.directory == directory) return;

		this.root = home;
		this.directory = directory;
		updateModel();
	}

	public DirectoryLocation getHomeDirectory(){
		return root;
	}

	private void updateModel(){
		TreeItem<LocationBreadCrumb> targetNode = null;
		TreeItem<LocationBreadCrumb> current = null;

		if(root != null && directory != null){
			DirectoryLocation dir = directory;

			boolean foundHome = false;
			while(dir != null){

				TreeItem<LocationBreadCrumb> p = new TreeItem<>(new LocationBreadCrumb(dir));
				if(current != null) 
					p.getChildren().add(current);
				else
					targetNode = p;
				current = p;

				if(dir.equals(root)){
					foundHome = true;
					break;
				}
				dir = dir.getParent();
			}

			if(!foundHome){
				// Curently, only debug output
				System.err.println("LocationBreadCrumbBarModel could not backtrack from directory to root dir:");
				System.err.println("Root  : " + root);
				System.err.println("Target: " + directory);
			}
		}
		TreeItem<LocationBreadCrumb> homeNode = new TreeItem<LocationBreadCrumb>(home);
		if(current != null) homeNode.getChildren().add(current);
		if(targetNode == null) targetNode = homeNode;

		breadCrumbBar.setSelectedCrumb(targetNode);
	}

}
