package vidada.model.libraries;

import java.beans.Transient;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.SessionManager;
import vidada.model.ServiceProvider;
import vidada.model.entities.BaseEntity;
import vidada.model.images.IImageService;
import vidada.model.images.cache.IImageCache;
import vidada.model.user.User;
import archimedesJ.io.locations.DirectoryLocation;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Represents an MediaLibrary
 * @author IsNull
 *
 */
public class MediaLibrary extends BaseEntity {

	private Set<LibraryEntry> libraryEntries = new HashSet<LibraryEntry>();
	private boolean ignoreMovies;
	private boolean ignoreImages;



	// TODO DEBUG ONLY!?
	transient private boolean useLibraryCache = true;

	transient private LibraryEntry currentEntry = null;
	transient private IImageCache imageCache = null;
	transient private MediaDirectory mediaDirectory = null;

	/**
	 *
	 */
	public MediaLibrary(){	
		//  Note: Empty constructor required by ORM
	}


	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public void setIgnoreMovies(boolean ignoreMovies) {
		this.ignoreMovies = ignoreMovies;
		mediaDirectory = null;
	}

	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages) {
		this.ignoreImages = ignoreImages;
		mediaDirectory = null;
	}


	/**
	 * Gets the media directory which represents the root of this media library
	 * @return
	 */
	public MediaDirectory getMediaDirectory(){

		if(mediaDirectory == null){
			mediaDirectory = new MediaDirectory(getLibraryRoot(), ignoreImages, ignoreMovies);
		}

		return mediaDirectory;
	}


	@Transient
	private DirectoryLocation getLibraryRoot() {
		DirectoryLocation root = null;
		LibraryEntry entry = getCurrentEntry();

		if(entry != null){
			root = getCurrentEntry().getLibraryRoot();
			if(root == null)
				System.err.println("library root is NULL!");
		}else {
			System.err.println("MediaLibrary: A LibraryEntry for the current user could not be found.");
		}
		return root;
	}

	/**
	 * Gets the libraries image cache
	 * @return Returns the cache service if this library supports caches
	 */
	public synchronized IImageCache getLibraryCache(){

		if(!useLibraryCache) return null;

		if(imageCache == null){

			IImageService imageService = ServiceProvider.Resolve(IImageService.class);

			DirectoryLocation libraryRoot = getLibraryRoot();
			if(libraryRoot != null && libraryRoot.exists()){
				try {
					DirectoryLocation libCache = DirectoryLocation.Factory.create(libraryRoot, "vidada.thumbs");
					System.out.println("opening new library cache...");
					imageCache = imageService.openCache(libCache);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
		return imageCache;
	}

	/**
	 * Is this library available?
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		DirectoryLocation root = getLibraryRoot();
		return root != null && root.exists();
	}




	@Transient
	private LibraryEntry getCurrentEntry(){
		if(currentEntry == null){
			User current = User.current();

			printAllEntrys();

			System.out.println("MediaLibrary: searching LibraryEntry for user: " + current);
			for (LibraryEntry entry : getLibraryEntries()) {
				System.out.println(entry);
				if(current.equals(entry.getUser())){
					currentEntry =  entry;
					System.out.println("^-- Matching Entry! --");
					break;
				}
			}
		}
		return currentEntry;
	}

	private void printAllEntrys(){

		System.out.println("All entries:");
		ObjectContainer db = SessionManager.getObjectContainer();

		Query query = db.query();
		query.constrain(LibraryEntry.class);
		List<LibraryEntry> entries = query.execute();

		for (LibraryEntry entry : entries) {
			System.out.println(entry.toString());
		}
	}



	public void addEntry(LibraryEntry e){
		libraryEntries.add(e);
	}

	public void removeEntry(LibraryEntry e){
		libraryEntries.remove(e);
	}

	public Set<LibraryEntry> getLibraryEntries() {
		return libraryEntries;
	}

	protected void setLibraryEntries(Set<LibraryEntry> libraryEntries) {
		this.libraryEntries = libraryEntries;
	}

	/**
	 * Set the root path of this media library
	 * @param libraryRoot
	 */
	public void setLibraryRoot(DirectoryLocation libraryRoot) {
		ObjectContainer db = SessionManager.getObjectContainer();
		LibraryEntry entry = findOrCreateEntry();

		entry.setLibraryRoot(libraryRoot);
		db.store(entry);
		db.commit();
	}


	/**
	 * Finds the current libraryEntry or creates a new one
	 * @return
	 */
	private LibraryEntry findOrCreateEntry(){

		LibraryEntry entry = this.getCurrentEntry();

		if(entry == null)
		{
			ObjectContainer db = SessionManager.getObjectContainer();
			{
				User user = User.current();
				entry = new LibraryEntry(this, user, null);
				db.store(entry);
				this.addEntry(entry);
				db.store(this);
			}
			db.commit();
		}

		return entry;
	}


	@Override
	public String toString(){
		DirectoryLocation root = getLibraryRoot();
		return root != null ? root.toString() : "DirectoiryLocation=NULL";
	}

}
