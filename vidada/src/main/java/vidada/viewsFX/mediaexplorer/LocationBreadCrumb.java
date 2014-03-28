package vidada.viewsFX.mediaexplorer;

import archimedes.core.io.locations.DirectoryLocation;

public class LocationBreadCrumb extends  SimpleBreadCrumbModel{
	private final DirectoryLocation location;

	public LocationBreadCrumb(DirectoryLocation location) {
		super(location != null ? location.getName() : "");
		this.location = location;
	}

	public DirectoryLocation getDirectoryLocation(){
		return location;
	}
}
