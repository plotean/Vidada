package vidada.viewsFX.mediaexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vidada.viewsFX.util.ObservableListProxy;
import javafx.collections.FXCollections;
import archimedesJ.io.locations.DirectoryLocation;

public class LocationBreadCrumbBarModel extends ObservableListProxy<LocationBreadCrumb> {

	private DirectoryLocation home;
	private DirectoryLocation directory;

	public LocationBreadCrumbBarModel(){
		super(FXCollections.observableArrayList(new ArrayList<LocationBreadCrumb>()));
	}

	public void setDirectory(DirectoryLocation home, DirectoryLocation directory){

		System.out.println("LocationBreadCrumbBarModel: " + directory);

		if(this.home == home && this.directory == directory) return;

		this.home = home;
		this.directory = directory;
		updateModel();
	}

	public DirectoryLocation getHomeDirectory(){
		return home;
	}

	private void updateModel(){
		clear();

		if(home != null && directory != null){

			List<LocationBreadCrumb> crumbs = new ArrayList<LocationBreadCrumb>();
			DirectoryLocation dir = directory;

			boolean foundHome = false;
			while(dir != null){
				crumbs.add(new LocationBreadCrumb(dir));
				if(dir.equals(home)){
					foundHome = true;
					break;
				}
				dir = dir.getParent();
			}
			if(foundHome){
				Collections.reverse(crumbs);
				addAll(crumbs);
			}else{
				System.err.println("LocationBreadCrumbBarModel could not backtrack from directory to home dir:");
				System.err.println("Home  : " + home);
				System.err.println("Target: " + directory);
			}
		}
	}

}
