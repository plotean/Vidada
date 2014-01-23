package vidada.viewsFX.mediaexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.controlsfx.control.breadcrumbs.BreadCrumbBar;

import archimedesJ.io.locations.DirectoryLocation;

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
		breadCrumbBar.getCrumbs().clear();

		List<LocationBreadCrumb> crumbs = new ArrayList<LocationBreadCrumb>();

		if(root != null && directory != null){
			DirectoryLocation dir = directory;

			boolean foundHome = false;
			while(dir != null){
				crumbs.add(new LocationBreadCrumb(dir));
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
		crumbs.add(home); // add home crumb

		Collections.reverse(crumbs);
		breadCrumbBar.getCrumbs().addAll(crumbs);
	}

}
