package vidada.model.libraries;

import archimedesJ.io.locations.DirectoiryLocation;
import vidada.model.entities.BaseEntity;
import vidada.model.user.User;

/**
 * Represents a library root path, coupled with the current user/environment
 * 
 * @author IsNull
 */
public class LibraryEntry extends BaseEntity {

	private DirectoiryLocation libraryRoot;
	private User user;
	private MediaLibrary parentLibrary;


	public LibraryEntry() {	}

	public LibraryEntry(MediaLibrary parentLibrary, User user, DirectoiryLocation libraryRoot) {	
		this.libraryRoot = libraryRoot;
		this.user = user;
		this.parentLibrary = parentLibrary;
	}

	public DirectoiryLocation getLibraryRoot() {
		return libraryRoot;
	}

	public void setLibraryRoot(DirectoiryLocation libraryRoot) {
		this.libraryRoot = libraryRoot;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User owner) {
		this.user = owner;
	}

	public MediaLibrary getParentLibrary() {
		return parentLibrary;
	}

	public void setParentLibrary(MediaLibrary parentLibrary) {
		this.parentLibrary = parentLibrary;
	}

}
