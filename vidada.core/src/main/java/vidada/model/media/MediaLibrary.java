package vidada.model.media;

import java.beans.Transient;
import java.net.URISyntaxException;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import vidada.model.entities.IdEntity;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.crypto.ImageCacheFactory;
import archimedesJ.io.locations.DirectoryLocation;

/**
 * Represents an local MediaLibrary
 * 
 * @author IsNull
 *
 */
@Entity
public class MediaLibrary extends IdEntity {

	public static final String VidataThumbsFolder = "vidada.thumbs";


	@Embedded
	private DirectoryLocation libraryRoot;
	private boolean ignoreMovies;
	private boolean ignoreImages;

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
		return libraryRoot;
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
	 * Is this library available?
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		DirectoryLocation root = getLibraryRoot();
		return root != null && root.exists();
	}

	/**
	 * Set the root path of this media library
	 * @param libraryRoot
	 */
	public void setLibraryRoot(DirectoryLocation libraryRoot) {
		this.libraryRoot = libraryRoot;
	}

	@Override
	public String toString(){
		DirectoryLocation root = getLibraryRoot();
		return root != null ? root.toString() : "MediaLibrary:: DirectoiryLocation=NULL";
	}

}
