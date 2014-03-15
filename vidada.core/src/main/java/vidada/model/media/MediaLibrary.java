package vidada.model.media;

import java.beans.Transient;
import java.io.File;
import java.net.URISyntaxException;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import vidada.model.entities.IdEntity;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.crypto.ImageCacheFactory;
import vidada.model.media.extracted.IMediaPropertyStore;
import vidada.model.media.extracted.MediaPropertyStore;
import archimedesJ.io.locations.DirectoryLocation;

/**
 * Represents an local user MediaLibrary folder.
 * 
 * @author IsNull
 *
 */
@Entity
@Access(AccessType.FIELD)
public class MediaLibrary extends IdEntity {

	/**
	 * Vidadas cache directory name in a users library folder root
	 */
	public static final String VidataCacheFolder = "vidada.db";
	public static final String VidataThumbsFolder = VidataCacheFolder + "/thumbs";
	public static final String VidataInfoFolder = VidataCacheFolder + "/info";
	public static final String VidataTagRelations = VidataCacheFolder + "/tags.txt";


	private String libraryRootURI;
	private boolean ignoreMovies;
	private boolean ignoreImages;

	transient private IImageCache imageCache = null;
	transient private IMediaPropertyStore propertyStore = null;
	transient private MediaDirectory mediaDirectory = null;
	transient private DirectoryLocation libraryDirectoryLocation = null;

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
	 * Gets the media directory which represents the root of this media library.
	 * @return
	 */
	public MediaDirectory getMediaDirectory(){

		if(mediaDirectory == null){
			mediaDirectory = new MediaDirectory(getLibraryRoot(), ignoreImages, ignoreMovies);
		}

		return mediaDirectory;
	}

	/**
	 * Set the root path of this media library
	 * @param libraryRoot
	 */
	@Transient
	public void setLibraryRoot(DirectoryLocation location) {
		this.libraryDirectoryLocation = location;
		libraryRootURI = libraryDirectoryLocation.getUri().toString();
	}


	@Transient
	private DirectoryLocation getLibraryRoot() {
		if(libraryDirectoryLocation == null){
			try {
				libraryDirectoryLocation = DirectoryLocation.Factory.create(libraryRootURI);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return libraryDirectoryLocation;
	}

	public File getUserTagRelationDef(){
		return new File(new File(getLibraryRoot().getPath()), VidataTagRelations);
	}

	/**
	 * Gets the libraries image cache
	 * @return Returns the cache service if this library supports caches
	 */
	public synchronized IImageCache getLibraryCache(){

		if(imageCache == null){
			DirectoryLocation libraryRoot = getLibraryRoot();
			if(libraryRoot != null && libraryRoot.exists()){
				try {
					DirectoryLocation libCache = DirectoryLocation.Factory.create(libraryRoot, VidataThumbsFolder);
					System.out.println("opening new library cache...");
					ImageCacheFactory factory = new ImageCacheFactory();
					imageCache = factory.openCache(libCache);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
		return imageCache;
	}

	/**
	 * Returns the property cache / store for this media library.
	 * 
	 * @return
	 */
	public synchronized IMediaPropertyStore getPropertyStore(){
		if(propertyStore == null){
			DirectoryLocation libraryRoot = getLibraryRoot();
			if(libraryRoot != null && libraryRoot.exists()){
				propertyStore = new MediaPropertyStore(new File(libraryRoot.getPath(), VidataInfoFolder));
			}
		}
		return propertyStore;
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

	@Override
	public String toString(){
		DirectoryLocation root = getLibraryRoot();
		return (root != null ? root.toString() : "MediaLibrary:: DirectoiryLocation=NULL") + " id: " + getId();
	}

}
