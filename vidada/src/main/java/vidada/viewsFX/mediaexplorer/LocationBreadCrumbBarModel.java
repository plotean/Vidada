package vidada.viewsFX.mediaexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import vidada.viewsFX.util.ObservableListProxy;
import archimedesJ.io.locations.DirectoryLocation;

public class LocationBreadCrumbBarModel extends ObservableListProxy<LocationBreadCrumb> {

	private final HomeLocationBreadCrumb home;

	private DirectoryLocation root;
	private DirectoryLocation directory;

	public LocationBreadCrumbBarModel(HomeLocationBreadCrumb home){
		super(FXCollections.observableArrayList(new ArrayList<LocationBreadCrumb>()));
		this.home = home;
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
		clear();

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
		addAll(crumbs);
	}

}
