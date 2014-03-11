package vidada.model.media.store.local;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import vidada.model.images.IThumbImageCreator;
import vidada.model.images.ThumbImageExtractor;
import vidada.model.media.MediaHashUtil;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaItemFactory;
import vidada.model.media.MediaLibrary;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.tags.autoTag.AutoTagSupport;
import vidada.model.tags.autoTag.ITagGuessingStrategy;
import vidada.model.tags.autoTag.KeywordBasedTagGuesser;
import vidada.server.services.IMediaLibraryService;
import vidada.server.services.IMediaService;
import vidada.server.services.ITagService;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;
import archimedesJ.util.Lists;

/**
 * This class implements basic media import functionality
 * @author IsNull
 *
 */
public class MediaImportStrategy implements IMediaImportStrategy {

	private final IMediaService mediaService;
	private final IMediaLibraryService libraryManager;
	private final ITagService tagService;

	private MediaHashUtil mediaHashUtil;
	private ITagGuessingStrategy tagguesser;

	private IThumbImageCreator thumbImageCreator =  new ThumbImageExtractor(); 


	public MediaImportStrategy(IMediaService mediaService, ITagService tagService, IMediaLibraryService libraryManager){
		this.mediaService = mediaService;
		this.tagService = tagService;
		this.libraryManager = libraryManager;
	}

	@Override
	public void scanAndUpdateDatabases(IProgressListener progressListener){

		mediaHashUtil =  MediaHashUtil.getDefaultMediaHashUtil();

		tagguesser = new KeywordBasedTagGuesser(tagService.getUsedTags()); 

		List<MediaLibrary> libraries = libraryManager.getAllLibraries();

		progressListener.currentProgress(new ProgressEventArgs(true, "Searching for all media files in your libraries (" + libraries.size()+")"));

		if(!libraries.isEmpty()){
			for (MediaLibrary lib : libraries) {
				if(lib.isAvailable())
				{
					scanAndUpdateLibrary(progressListener, lib);
				}
			};

			progressListener.currentProgress(new ProgressEventArgs(100, "Done."));
		}else {
			System.out.println("Import aborted, you dont have any MediaLibraries!");
		}
	}


	/**
	 * 
	 * @param progressListener
	 * @param library
	 */
	@Override
	public void scanAndUpdateLibrary(IProgressListener progressListener, MediaLibrary library)
	{
		progressListener.currentProgress(new ProgressEventArgs(true, "Scanning for media files in " + library + " ..."));

		List<ResourceLocation> mediafiles = library.getMediaDirectory().getAllMediaFilesRecursive();


		if(!mediafiles.isEmpty())
			progressListener.currentProgress(new ProgressEventArgs(true, "Media files found:\t " + mediafiles.size()));

		//
		// analyze all the existing files inside this library
		//
		Map<ResourceLocation, String> analyzedFiles = scanPhysicalFiles(progressListener, mediafiles);

		if(analyzedFiles != null)
		{
			//
			// now lets check against our db
			//
			Map<String, ResourceLocation> newFiles = compareWithExisting(progressListener, analyzedFiles, library);

			progressListener.currentProgress(new ProgressEventArgs(true, "Starting import of new files..."));

			importNewFiles(progressListener, library, newFiles);
		}

		else
			progressListener.currentProgress(new ProgressEventArgs(100, "Found NO medifiles in library " + library + "!"));
	}


	/**
	 * Find and analyze the content of all files in the given folder
	 * 
	 * @param progressListener
	 * @param library
	 * @return
	 */
	private Map<ResourceLocation, String> scanPhysicalFiles(IProgressListener progressListener, List<ResourceLocation> mediafiles){
		Map<ResourceLocation, String> filecontentMap = new HashMap<ResourceLocation, String>();

		for (ResourceLocation file : mediafiles) {
			filecontentMap.put(file, null);
		}

		progressListener.currentProgress(new ProgressEventArgs(true, "Analyzing file contents..."));

		ResourceLocation[] files = filecontentMap.keySet().toArray(new ResourceLocation[0]);
		String hash;

		for (int i = 0; i < files.length; i++) {
			hash = mediaHashUtil.retriveFileHash(files[i]);
			filecontentMap.put(files[i], hash);

			progressListener.currentProgress(new ProgressEventArgs(100 / files.length * i, "hash: " + hash + "\tfile: " + files[i].getName()));
		}

		return filecontentMap;
	}


	private Map<String, MediaItem> fetchCurrentMedias(){

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
		Map<String, MediaItem> existingMediaData = fetchCurrentMedias();

		progressListener.currentProgress(new ProgressEventArgs(true, "Retriving ObjectContainer..."));

		try{
			progressListener.currentProgress(new ProgressEventArgs(true, "Checking " + existingMediaData.size() + " medias..."));
			Collection<MediaItem>  mediashm = existingMediaData.values();
			List<MediaItem> medias = new ArrayList<MediaItem>(mediashm);

			for (MediaItem mediaData : medias) {
				// check if the parent library is still correct
				if(isMemberofLibrary(mediaData, library))
				{
					// add the media data to the probably existing, but default the exists to false
					realExistingMediaDatas.put(mediaData, false);

					// update resolution
					if(!mediaData.hasResolution()){
						thumbImageCreator.updateResolution(mediaData);
						updateMedias.add(mediaData);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		//
		progressListener.currentProgress(new ProgressEventArgs(true, "Compare the " + fileContentMap.size() + " physical existing files with the current index media items"));

		int i = 0;
		double fileMapSize = fileContentMap.size();
		for (Entry<ResourceLocation, String> entry : fileContentMap.entrySet()) {

			if(existingMediaData.containsKey(entry.getValue()))
			{	
				MediaItem existingMeida = existingMediaData.get(entry.getValue());
				realExistingMediaDatas.put(existingMeida, true); // mark the media data as real existing

				//
				// this file hash was already present in our media lib. 
				// check if the details are still the same and update it if necessary
				//
				if(updateExistingMedia(library, existingMeida, entry))
					updateMedias.add(existingMeida);

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
	 * @param em
	 * @param media
	 */
	private boolean canMediaBeDeleted(MediaLibrary library, MediaItem media) {

		System.out.println("handleNonExistingMedia: " + media );

		// Remove non existing file sources
		Set<MediaSource> srcs = media.getSources();
		for (MediaSource msource : srcs) {
			MediaSourceLocal source = (MediaSourceLocal)msource;
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
	private boolean isMemberofLibrary(MediaItem media, MediaLibrary library) {
		if(library == null) throw new IllegalArgumentException("library must not be NULL!");

		for (MediaSource s : media.getSources()) {
			if(s != null && s instanceof MediaSourceLocal){
				MediaLibrary parentLib = ((MediaSourceLocal)s).getParentLibrary();
				if(parentLib != null){
					return parentLib.equals(library);
				}else{
					System.err.println("MediaItem::isMemberofLibrary: parent library of " + s + " was NULL!");
				}
			}else{
				System.err.println("MediaItem::isMemberofLibrary: media source of " + this + " was NULL!");
			}
		}
		return false;
	}


	/**
	 * Update a existing media file
	 * @param em
	 * @param library
	 * @param existingMeida
	 * @param entry
	 * @return
	 */
	private boolean updateExistingMedia(MediaLibrary library, MediaItem existingMeida, Entry<ResourceLocation, String> entry){
		boolean hasChanges = false;


		updateExistingMediaSources(library, existingMeida, entry.getKey());

		// Update Tags From file path
		if(tagguesser != null && AutoTagSupport.updateTags(tagguesser, existingMeida)){
			hasChanges = true;
		}

		return hasChanges;
	}

	/**
	 * Update the media sources of the given media library
	 * (That means that current paths will be updated)
	 * @param em
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
	 * @param newfilesWithHash New file tuples, with precalculated file content hashes
	 */
	private void importNewFiles(IProgressListener progressListener, MediaLibrary parentlibrary, Map<String, ResourceLocation> newfilesWithHash){
		progressListener.currentProgress(new ProgressEventArgs(true, "Importing " + newfilesWithHash.size() + " new files..."));

		List<MediaItem> newmedias = new ArrayList<MediaItem>(newfilesWithHash.size());

		double fileMapSize = newfilesWithHash.size();

		int i=0;
		for (Entry<String, ResourceLocation> entry : newfilesWithHash.entrySet()) {

			int progress = (int)(100d / fileMapSize * (double)i);
			progressListener.currentProgress(new ProgressEventArgs(progress, "Importing new media:\t" + entry.getValue().getName()));

			MediaItem newDataPart = MediaItemFactory.instance().buildMedia(entry.getValue(), parentlibrary, entry.getKey());

			if(newDataPart != null)
			{
				if(tagguesser != null)
					AutoTagSupport.updateTags(tagguesser, newDataPart);
				newmedias.add(newDataPart);
			}
			i++;
		}

		String msg = "Adding " + newmedias.size() + " new medias to the Library...";
		progressListener.currentProgress(new ProgressEventArgs(true, msg));
		mediaService.store(newmedias);
	}





}
