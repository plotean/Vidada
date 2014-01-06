package vidada.viewsFX.mediaexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vidada.viewsFX.breadcrumbs.BreadCrumbBarModel;
import vidada.viewsFX.breadcrumbs.IBreadCrumbModel;
import archimedesJ.io.locations.DirectoryLocation;

public class LocationBreadCrumbBarModel extends BreadCrumbBarModel{

	private DirectoryLocation home;
	private DirectoryLocation directory;

	public void setDirectory(DirectoryLocation home, DirectoryLocation directory){

		System.out.println("LocationBreadCrumbBarModel: " + directory);

		if(this.home == home && this.directory == directory) return;

		this.home = home;
		this.directory = directory;
		updateModel();
	}

	@Override
	protected void onBreadCrumbOpenRequest(IBreadCrumbModel crumb){
		super.onBreadCrumbOpenRequest(crumb);

		if(crumb instanceof LocationBreadCrumb){
			DirectoryLocation dir = ((LocationBreadCrumb)crumb).getDirectoryLocation();
			setDirectory(getHomeDirectory(), dir);
		}else{
			System.err.println("LocationBreadCrumbBarModel::onBreadCrumbOpenRequest: "
					+ "Got children which are not LocationBreadCrumb but: " + crumb);
		}
	}

	public DirectoryLocation getHomeDirectory(){
		return home;
	}

	private void updateModel(){
		clear();

		if(home != null && directory != null){

			List<IBreadCrumbModel> crumbs = new ArrayList<IBreadCrumbModel>();
			DirectoryLocation dir = directory;

			System.out.println("LocationBreadCrumbBarModel: searching home... ");

			boolean foundHome = false;
			while(dir != null){
				crumbs.add((IBreadCrumbModel)new LocationBreadCrumb(dir));
				if(dir.equals(home)){
					foundHome = true;
					break;
				}else{
					System.out.println("Not Equal:");
					System.out.println("Home  : " + home);
					System.out.println("Target: " + dir);
				}
				dir = dir.getParent();
			}
			System.out.println("LocationBreadCrumbBarModel: searching home done.");
			if(foundHome){
				Collections.reverse(crumbs);
				this.add(crumbs);
			}else{
				System.err.println("LocationBreadCrumbBarModel could not backtrack from directory to home dir:");
				System.err.println("Home  : " + home);
				System.err.println("Target: " + directory);
			}
		}
	}

}
