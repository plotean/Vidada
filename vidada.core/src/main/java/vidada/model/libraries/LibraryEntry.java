package vidada.model.libraries;

import vidada.model.entities.BaseEntity;
import vidada.model.user.User;
import archimedesJ.io.locations.DirectoiryLocation;

import com.db4o.foundation.ArgumentNullException;

/**
 * Represents a library root path, coupled with the current user/environment
 * 
 * @author IsNull
 */
public class LibraryEntry extends BaseEntity {

	private User user;
	private MediaLibrary parentLibrary;

	private DirectoiryLocation libraryRoot;


	/**
	 * Creates a new LibraryEntry
	 * 
	 * @param parentLibrary The parent library. Must not be Null.
	 * @param user The user to which this entry belongs. Must not be Null.
	 * @param libraryRoot The root folder of the library.
	 * 
	 * @throws ArgumentNullException
	 */
	public LibraryEntry(MediaLibrary parentLibrary, User user, DirectoiryLocation libraryRoot){

		/*
		if(user == null)
			throw new ArgumentNullException("user");
		if(parentLibrary == null)
			throw new ArgumentNullException("parentLibrary");
		 */

		this.user = user;
		this.parentLibrary = parentLibrary;
		this.libraryRoot = libraryRoot;
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

	public MediaLibrary getParentLibrary() {
		return parentLibrary;
	}

	@Override
	public String toString(){
		return "Entry[" + getId() +", " + getUser() + "]";
	}

	/*
	@Override
	public String toString(){
		return "Entry[" + getId() + ", " + getParentLibrary() +", " + getUser() + "]";
	}*/

}
