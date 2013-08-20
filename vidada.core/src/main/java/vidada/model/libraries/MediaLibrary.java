package vidada.model.libraries;

import java.beans.Transient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.data.SessionManager;
import vidada.model.entities.BaseEntity;
import vidada.model.media.MediaFileInfo;
import vidada.model.media.MediaType;
import vidada.model.user.User;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.io.locations.ILocationFilter;
import archimedesJ.io.locations.LocationFilters;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.io.locations.UniformLocation;
import archimedesJ.util.FileSupport;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;

/**
 * Represents an MediaLibrary
 * @author IsNull
 *
 */
public class MediaLibrary extends BaseEntity {

	private Set<LibraryEntry> libraryEntries = new HashSet<LibraryEntry>();

	private boolean ignoreMovies;
	private boolean ignoreImages;

	transient private LibraryEntry currentEntry = null;
	transient private ILocationFilter mediaFilter = null;

	/**
	 * 
	 */
	public MediaLibrary(){	}


	@Transient
	public DirectoiryLocation getLibraryRoot() {
		DirectoiryLocation root = null;
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
	 * Is this library available?
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		DirectoiryLocation root = getLibraryRoot();
		return root != null && root.exists();
	}




	@Transient
	private LibraryEntry getCurrentEntry(){
		if(currentEntry == null){
			User current = User.current();
			for (LibraryEntry entry : getLibraryEntries()) {
				if(entry.getUser().equals(current)){
					currentEntry =  entry;
					break;
				}
			}
		}
		return currentEntry;
	}

	/**
	 * Constructs the relative path for the given file (must be a sub dir of this lib!)
	 * @param absoluteLibraryFile
	 * @return Returns the relative path or null, if a relative path could not be created
	 */
	@Transient
	public URI getRelativePath(ResourceLocation absoluteLibraryFile){

		URI relativeFile = FileSupport.getRelativePath(
				absoluteLibraryFile.getUri(),
				getLibraryRoot().getUri());

		if(absoluteLibraryFile.getUri().getPath().equals(relativeFile.getPath()))
			relativeFile = null;

		if(relativeFile == null)
		{
			System.err.println("could not create relative path for:");
			System.err.println("absolute: " + absoluteLibraryFile.getUri());
			System.err.println("root: " + getLibraryRoot().getUri());
		}

		return relativeFile;
	}

	/**
	 * Returns the absolute path for the given relative file path
	 * @param relativeFile
	 * @return
	 */
	@Transient
	public ResourceLocation getAbsolutePath(URI relativeFile){		
		try {
			DirectoiryLocation location = getLibraryRoot();

			if(location != null){
				return  ResourceLocation.Factory.create(
						new URI(location.getUri() + relativeFile.toString()),
						location.getCreditals());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		return null;
	}

	/**
	 * Returns all Files from this library
	 * @return
	 */
	@Transient
	public List<ResourceLocation> getAllFiles(){
		return Lists.asTypedList(getLibraryRoot().listAll(LocationFilters.AcceptAllFiles, true));
	}

	@Transient
	public List<ResourceLocation> getAllFiles(ILocationFilter filter){
		DirectoiryLocation root = getLibraryRoot();
		System.out.println("MediaLibrary: searching for all files in root: " + root.toString());
		return Lists.asTypedList(root.listAll(filter, true));
	}

	@Transient
	public List<ResourceLocation> getAllMediaFiles(DirectoiryLocation root){
		return Lists.asTypedList(root.listAll(buildFilter(), true));
	}



	/**
	 * A simple filter to igonre meta data files (AppleDouble etc.)
	 */
	private transient static final ILocationFilter ignoreMetaDataFilter = new ILocationFilter() {

		private static final String AppleDoublePrefix = "._";
		private final int AppleDoublePrefixLen = AppleDoublePrefix.length();


		@Override
		public boolean accept(UniformLocation file) {


			if(file instanceof ResourceLocation)
			{
				String name = ((ResourceLocation)file).getName();

				if(name.length() >= AppleDoublePrefixLen){
					return !name.substring(0, AppleDoublePrefixLen).equals(AppleDoublePrefix);
				}
			}

			return true;
		}
	};

	/**
	 * Build an IOFilter to filter files
	 * which are applicable for this media library
	 * @return
	 */
	@Transient
	public ILocationFilter buildFilter(){

		if(mediaFilter == null){
			String[] allMediaExtensions = new String[0];

			if(!this.isIgnoreImages())
			{
				allMediaExtensions = Lists.concat(
						allMediaExtensions, 
						MediaFileInfo.get(MediaType.IMAGE).getFileExtensions());
			}

			if(!this.isIgnoreMovies())
			{
				allMediaExtensions = Lists.concat(
						allMediaExtensions, 
						MediaFileInfo.get(MediaType.MOVIE).getFileExtensions());
			}

			mediaFilter = LocationFilters.extensionFilter(allMediaExtensions);
			mediaFilter = LocationFilters.and(mediaFilter, ignoreMetaDataFilter);
		}
		return mediaFilter;
	}

	/**
	 * Returns a filter who accepts all media files
	 * 
	 * Note:
	 * The files are filtered by extension. Additionally, special meta data
	 * files are suppressed.
	 * @return
	 */
	@Transient
	public static ILocationFilter buildAllMediaFilter(){
		String[] extensions = MediaFileInfo.getAllMediaExtensions();
		ILocationFilter extensionFilter = LocationFilters.extensionFilter(extensions);
		return LocationFilters.and(extensionFilter, ignoreMetaDataFilter);
	}

	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public void setIgnoreMovies(boolean ignoreMovies) {
		this.ignoreMovies = ignoreMovies;
	}

	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages) {
		this.ignoreImages = ignoreImages;
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


	@Override
	public String toString(){
		DirectoiryLocation root = getLibraryRoot();
		return root != null ? root.toString() : "DirectoiryLocation=NULL";
	}

	/**
	 * Set the root path of this media library
	 * @param libraryRoot
	 */
	public void setLibraryRoot(DirectoiryLocation libraryRoot) {
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

			User user = User.current();
			entry = new LibraryEntry(this, user, null);
			db.store(entry);
			this.addEntry(entry);
			db.store(this);
		}

		return entry;
	}
}
