package vidada.viewsFX.mediaexplorer;

import vidada.viewsFX.breadcrumbs.SimpleBreadCrumbModel;
import archimedesJ.io.locations.DirectoryLocation;

public class LocationBreadCrumb extends SimpleBreadCrumbModel {
	private final DirectoryLocation location;

	public LocationBreadCrumb(DirectoryLocation location) {
		super(location.getName());
		this.location = location;
	}

	public DirectoryLocation getDirectoryLocation(){
		return location;
	}

	@Override
	public String toString(){
		return super.toString() + getDirectoryLocation();
	}
}
