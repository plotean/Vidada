package vidada.model.media;

import java.beans.Transient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import vidada.model.media.store.libraries.MediaLibrary;
import archimedesJ.io.locations.DirectoryLocation;
import archimedesJ.io.locations.IDirectoryFilter;
import archimedesJ.io.locations.ILocationFilter;
import archimedesJ.io.locations.LocationFilters;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.io.locations.UniformLocation;
import archimedesJ.util.FileSupport;
import archimedesJ.util.Lists;

/**
 * Represents an abstract directory containing media files
 * Provides convenient methods to (recursively) list all matching media items.
 * 
 * This implementation is immutable
 * @author IsNull
 *
 */
public final class MediaDirectory {

	private final DirectoryLocation directory;

	private final boolean ignoreMovies;
	private final boolean ignoreImages;

	transient private ILocationFilter mediaFilter = null;


	/**
	 * Creates a new MediaDirectory with the specified filter settings.
	 * 
	 * @param directory Root directory
	 * @param ignoreImages Ignore images files
	 * @param ignoreMovies Inore movie files
	 */
	public MediaDirectory(DirectoryLocation directory, boolean ignoreImages, boolean ignoreMovies) { 
		this.directory = directory;
		this.ignoreImages = ignoreImages;
		this.ignoreMovies = ignoreMovies;
	}


	public DirectoryLocation getDirectory() {
		return directory;
	}


	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	/**
	 * Is this library available?
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		return directory != null && directory.exists();
	}

	/**
	 * Constructs the relative path for the given file (must be in a sub directory of this directory!)
	 * @param absoluteLibraryFile
	 * @return Returns the relative path or null, if a relative path could not be created
	 */
	@Transient
	public URI getRelativePath(ResourceLocation absoluteLibraryFile){

		URI relativeFile = FileSupport.getRelativePath(
				absoluteLibraryFile.getUri(),
				directory.getUri());

		if(absoluteLibraryFile.getUri().getPath().equals(relativeFile.getPath()))
			relativeFile = null;

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

			if(directory != null){
				return  ResourceLocation.Factory.create(
						new URI(directory.getUri() + relativeFile.toString()),
						directory.getCreditals());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		return null;
	}



	/**
	 * Returns all media files recursively in this library
	 * @return
	 */
	public List<ResourceLocation> getAllMediaFilesRecursive(){
		if(directory != null)
			return Lists.asTypedList(directory.listAll(buildFilter(), ignoreVidadaThumbs));
		return new ArrayList<ResourceLocation>();
	}

	/**
	 * Returns all media files and folders in this directory
	 * @return
	 */
	public List<UniformLocation> getMediasAndFolders(){
		if(directory != null){

			ILocationFilter filter = LocationFilters.or(
					LocationFilters.AcceptAllDirs,
					buildFilter());

			return Lists.asTypedList(directory.listAll(filter));
		}
		return new ArrayList<UniformLocation>();
	}


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


	/**
	 * A simple filter to ignore meta data files (AppleDouble etc.)
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
	 * A simple filter to igonre vidada generated thumbs
	 */
	private transient static final IDirectoryFilter ignoreVidadaThumbs = new IDirectoryFilter() {
		@Override
		public boolean accept(DirectoryLocation directoiry) {
			String name = directoiry.getName();
			return !name.equals(MediaLibrary.VidataThumbsFolder);
		}
	};

}
