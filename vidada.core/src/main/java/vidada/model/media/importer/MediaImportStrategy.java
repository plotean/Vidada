package vidada.model.media.importer;

import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;
import archimedes.core.util.Lists;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaItemFactory;
import vidada.model.media.MediaLibrary;
import vidada.model.media.info.IMediaInfoUpdateService;
import vidada.model.media.info.MediaInfoUpdateService;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.tags.autoTag.AutoTagSupport;
import vidada.model.tags.autoTag.ITagGuessingStrategy;
import vidada.model.tags.autoTag.KeywordBasedTagGuesser;
import vidada.server.services.IMediaService;
import vidada.server.services.ITagService;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class implements basic media import functionality
 * to import MediaLibraries.
 *
 * You should not cache instances of this class, instead
 * create a new instance for each Import for most accurate results.
 * (This class caches a lot of environment states when being created)
 *
 * @author IsNull
 *
 */
public class MediaImportStrategy implements IMediaImportStrategy {

    /***************************************************************************
     *                                                                         *
     * Private final fields                                                    *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaImportStrategy.class.getName());

    private final IMediaService mediaService;
	private final ITagService tagService;
	private final MediaHashUtil mediaHashUtil;
	private final ITagGuessingStrategy tagGuessingStrategy;
	private final IMediaInfoUpdateService mediaInfoUpdateService =  new MediaInfoUpdateService();
    private final List<MediaLibrary> libraries = new ArrayList<MediaLibrary>();

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private Map<String, MediaItem> existingMediaData;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaImportStrategy to synchronize the specified libraries.
     *
     * @param mediaService
     * @param tagService
     * @param libraries
     */
	public MediaImportStrategy(IMediaService mediaService, ITagService tagService, List<MediaLibrary> libraries){
		this.mediaService = mediaService;
		this.tagService = tagService;
        this.mediaHashUtil =  MediaHashUtil.getDefaultMediaHashUtil();
        this.tagGuessingStrategy = new KeywordBasedTagGuesser(tagService.getAllTags());

		this.libraries.addAll(libraries);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**{@inheritDoc}*/
	@Override
	public void synchronize(IProgressListener progressListener){
        try {
            // Init
            existingMediaData = fetchExistingMedias();

            progressListener.currentProgress(new ProgressEventArgs(true, "Searching for all media files in your libraries (" + libraries.size() + ")"));

            if (!libraries.isEmpty()) {
                for (MediaLibrary lib : libraries) {
                    if (lib.isAvailable()) {
                        synchronizeLibrary(progressListener, lib);
                    }
                }

                progressListener.currentProgress(new ProgressEventArgs(100, "Done."));
            } else {
                logger.info("Import aborted, you do not have specified any libraries!");
            }
        }finally {
            progressListener.currentProgress(ProgressEventArgs.COMPLETED);
        }
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	/**
	 * Synchronizes all Medias which belong to the given {@link MediaLibrary}
	 * @param progressListener
	 * @param library
	 */
	private void synchronizeLibrary(IProgressListener progressListener, MediaLibrary library)
	{
		progressListener.currentProgress(new ProgressEventArgs(true, "Scanning for media files in " + library + " ..."));

		List<ResourceLocation> mediaLocations = library.getMediaDirectory().getAllMediaFilesRecursive();


		if(!mediaLocations.isEmpty()) {
            progressListener.currentProgress(new ProgressEventArgs(true, "Media files found:\t " + mediaLocations.size()));

            Map<ResourceLocation, String> analyzedFiles = createFileHashCache(progressListener, mediaLocations);

            //
            // now lets check against existing medias in our database
            //
            Map<String, ResourceLocation> newFiles = compareWithExisting(progressListener, analyzedFiles, library);

            // Import the new found files
            progressListener.currentProgress(new ProgressEventArgs(true, "Starting import of new files..."));
            importNewFiles(progressListener, library, newFiles);

        }else{
            progressListener.currentProgress(new ProgressEventArgs(true, "No media files found in library " + library));
        }
	}


	/**
	 * Creates a Cache (Map) which holds the hash for each physical file.
	 * 
	 * @param progressListener
	 * @param mediaLocations
	 * @return
	 */
	private Map<ResourceLocation, String> createFileHashCache(IProgressListener progressListener, List<ResourceLocation> mediaLocations){
		Map<ResourceLocation, String> fileContentMap = new HashMap<ResourceLocation, String>();

		progressListener.currentProgress(new ProgressEventArgs(true, "Analyzing file contents..."));

		String hash;
        int locationCount = mediaLocations.size();
		for (int i = 0; i < locationCount; i++) {
            ResourceLocation location = mediaLocations.get(i);

			hash = mediaHashUtil.retrieveFileHash(location);
            fileContentMap.put(location, hash);
			progressListener.currentProgress(new ProgressEventArgs(100 / locationCount * i, "hash: " + hash + "\tfile: " + location.getName()));
		}

		return fileContentMap;
	}


	private Map<String, MediaItem> fetchExistingMedias(){

		Map<String, MediaItem> existingMediaData = new HashMap<String, MediaItem>();

		List<MediaItem> knownMedias = mediaService.getAllMedias();

		for (MediaItem mediaData : knownMedias) {
			existingMediaData.put(mediaData.getFilehash(), mediaData);
		}
		return existingMediaData;
	}



	/**
	 * Compare the current existing files with the ones already indexed inside the database.
	 * 
	 * @param progressListener
	 * @return returns the found new (not indexed) files
	 */
	private Map<String, ResourceLocation> compareWithExisting(IProgressListener progressListener, Map<ResourceLocation, String> fileContentMap, MediaLibrary library){


		progressListener.currentProgress(new ProgressEventArgs(true, "Comparing with current db..."));

		Map<String, ResourceLocation> newFiles = new HashMap<String, ResourceLocation>();
		Set<MediaItem> removeMedias = new HashSet<MediaItem>();
		Set<MediaItem> updateMedias = new HashSet<MediaItem>();

		Map<MediaItem, Boolean> realExistingMediaDatas = new HashMap<MediaItem, Boolean>();

		progressListener.currentProgress(new ProgressEventArgs(true, "Retrieving ObjectContainer..."));

		try{
			progressListener.currentProgress(new ProgressEventArgs(true, "Checking " + existingMediaData.size() + " medias..."));
			Collection<MediaItem>  existingMedias = existingMediaData.values();

			for (MediaItem existingMedia : existingMedias) {
				// check if the parent library is still correct
				if(isMemberOfLibrary(existingMedia, library))
				{
					// add the media data to the probably existing, but default the exists to false
					realExistingMediaDatas.put(existingMedia, false);
				}
			}
		}catch(Exception e){
            logger.error(e);
			return null;
		}

		progressListener.currentProgress(new ProgressEventArgs(true, "Compare the " + fileContentMap.size() + " physical existing files with the current index media items"));

		int i = 0;
		double fileMapSize = fileContentMap.size();
		for (Entry<ResourceLocation, String> entry : fileContentMap.entrySet()) {

			if(existingMediaData.containsKey(entry.getValue()))
			{	
				MediaItem existingMedia = existingMediaData.get(entry.getValue());
				realExistingMediaDatas.put(existingMedia, true); // mark the media data as real existing

				//
				// this file hash was already present in our media lib. 
				// check if the details are still the same and update it if necessary
				//
				if(updateExistingMedia(existingMedia, library, entry))
					updateMedias.add(existingMedia);

			}else{
				newFiles.put(entry.getValue(), entry.getKey());
			}

			int progress = (int)(100d / fileMapSize * (double)i);
			progressListener.currentProgress(new ProgressEventArgs(progress, "Importing:\t" + entry.getKey().getName()));
			i++;
		}

		// collect the no longer existing media files in the current media library
		for (Entry<MediaItem, Boolean> mediaDataExistence : realExistingMediaDatas.entrySet()) {
			if(!mediaDataExistence.getValue()){
				MediaItem m =  mediaDataExistence.getKey();
				if(canMediaBeDeleted(library, m)){
					removeMedias.add(m);
				}
			}
		}

		// bulk update 
		mediaService.delete(removeMedias);
		mediaService.update(updateMedias);

		return newFiles;
	}

	/**
	 * Remove the no longer existing media source
	 * @param media
	 */
	private boolean canMediaBeDeleted(MediaLibrary library, MediaItem media) {

        logger.trace("handleNonExistingMedia: " + media );

		// Remove non existing file sources
		Set<MediaSource> allSources = media.getSources();
		for (MediaSource currentSource : allSources) {
			MediaSourceLocal source = (MediaSourceLocal)currentSource;
			if(source.getParentLibrary().equals(library))
			{
				media.removeSource(source);
			}
		}
		// If the media has no sources left, we mark it as to be deleted
		return (media.getSources().size() == 0);
	}

	/**
	 * Is this media a member of the given library?
	 * 
	 * @param library
	 * @return
	 */
	private boolean isMemberOfLibrary(MediaItem media, MediaLibrary library) {
		if(library == null) throw new IllegalArgumentException("library must not be NULL!");

		for (MediaSource s : media.getSources()) {
			if(s != null && s instanceof MediaSourceLocal){
				MediaLibrary parentLib = ((MediaSourceLocal)s).getParentLibrary();
				if(parentLib != null){
					return parentLib.equals(library);
				}else{
                    logger.error("Parent library of " + s + " was NULL!");
				}
			}else{
                logger.error("MediaSource of " + this + " was NULL!");
			}
		}
		return false;
	}


	/**
	 * Update a existing media item
	 * @param existingMedia
	 * @param parentLibrary
	 * @param entry
	 * @return Returns true if any property of this media has been updated
	 */
	private boolean updateExistingMedia(MediaItem existingMedia, MediaLibrary parentLibrary, Entry<ResourceLocation, String> entry){

        boolean hasChanges = updateExistingMediaSources(parentLibrary, existingMedia, entry.getKey());

		// Update Tags From file path
		if(tagGuessingStrategy != null && AutoTagSupport.updateTags(tagGuessingStrategy, existingMedia)){
			hasChanges = true;
		}

        if(updateMediaProperties(existingMedia, parentLibrary)){
            hasChanges = true;
        }

		return hasChanges;
	}

	/**
	 * Update the media sources of the given media library
	 * (That means that current paths will be updated)
	 * @param library
	 * @param existingMeida
	 * @param currentPath
	 * @return
	 */
	private boolean updateExistingMediaSources(MediaLibrary library, MediaItem existingMeida, ResourceLocation currentPath){
		boolean hasChanges = false;

		boolean currentPathExisits = false;

		for (MediaSource msource : Lists.newList(existingMeida.getSources())) {

			MediaSourceLocal source = (MediaSourceLocal)msource;

			if(source.getParentLibrary() == null){
				existingMeida.removeSource(source);
				hasChanges = true;
			}else if(source.getParentLibrary().equals(library))
			{
				// we only care about the current library-
				source.setIsAvailableDirty();

				if(!source.isAvailable()){
					logger.debug("Removing old source: " + source);
					existingMeida.removeSource(source);
					hasChanges = true;
				}else{
					if(source.getRelativeFilePath().equals(library.getMediaDirectory().getRelativePath(currentPath)))
					{
						currentPathExisits = true;
					}
				}
			}
		}

		// if the media has not yet this library as source, try to add it as one
		if(!currentPathExisits)
		{
			URI relativePath = library.getMediaDirectory().getRelativePath(currentPath);
			logger.debug("trying to add new source: " + relativePath);
			if(relativePath != null){
				MediaSourceLocal source = new MediaSourceLocal(library, relativePath);
				existingMeida.addSource(source);
				hasChanges = true;
			}
		}

		return hasChanges;
	}



	/**
	 * Import the new found media files
	 * @param progressListener
	 * @param newfilesWithHash New file tuples, with pre-calculated file content hashes
	 */
	private void importNewFiles(IProgressListener progressListener, MediaLibrary parentlibrary, Map<String, ResourceLocation> newfilesWithHash){
		progressListener.currentProgress(new ProgressEventArgs(true, "Importing " + newfilesWithHash.size() + " new files..."));

		List<MediaItem> newMedias = new ArrayList<MediaItem>(newfilesWithHash.size());

		double fileMapSize = newfilesWithHash.size();

		int i=0;
		for (Entry<String, ResourceLocation> entry : newfilesWithHash.entrySet()) {

			int progress = (int)(100d / fileMapSize * (double)i);
			progressListener.currentProgress(new ProgressEventArgs(progress, "Importing new media:\t" + entry.getValue().getName()));
			MediaItem newMedia = MediaItemFactory.instance().buildMedia(entry.getValue(), parentlibrary, entry.getKey());

			if(newMedia != null)
			{
				// Add tags guessed from the file structure
				if(tagGuessingStrategy != null)
					AutoTagSupport.updateTags(tagGuessingStrategy, newMedia);

                updateMediaProperties(newMedia, parentlibrary);
                newMedias.add(newMedia);
			}
			i++;
		}

		progressListener.currentProgress(new ProgressEventArgs(true, "Adding " + newMedias.size() + " new medias to the Library..."));
		mediaService.store(newMedias);
	}

    /**
     * Updates all necessary properties of this media
     * (file size, added date, resolution, duration etc)
     * if they are not yet present.
     *
     *
     * @param media
     * @return
     */
    private boolean updateMediaProperties(MediaItem media, MediaLibrary parentLibrary){

        boolean hasChanges = false;
        hasChanges = MediaItemFactory.instance().updateBasicAttributes(media);

        // TODO Handle more complex properties such as duration/bitrate etc.
        // Add media infos which might be present from previous caches
        mediaInfoUpdateService.updateInfoFromCache(media, parentLibrary.getPropertyStore());

        return hasChanges;
    }


}
