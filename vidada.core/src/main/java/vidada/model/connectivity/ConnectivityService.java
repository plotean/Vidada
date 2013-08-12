package vidada.model.connectivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.tags.Tag;
import vidada.model.tags.TagLookUpCache;
import archimedesJ.util.FileSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Holds some basic media data exchange functionality
 * - exporting and importing media info
 * 
 * @author IsNull
 *
 */
public class ConnectivityService implements IConnectivityService {

	private IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	/**
	 * Create missing Tags on media info import?
	 */
	private boolean createMissingTags = true;


	@Override
	public void exportMediaInfo(File exportDesitination) {

		MediaDataInfoPack mediaInfoPack = new MediaDataInfoPack(generateDTOs());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(mediaInfoPack);

		try {
			FileSupport.writeToFile(exportDesitination, jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateMediaDatas(File importTags){
		try {
			String mediaInfoJson = FileSupport.readFileToString(importTags);
			updateMediaDatas(mediaInfoJson);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void updateMediaDatas(String mediaInfoJson){

		List<MediaDataInfo> mediainfos = parseJsonMediaInfo(mediaInfoJson);

		System.out.println("found " + mediainfos.size() + " media infos to import.");

		if(mediainfos != null){
			Map<String, MediaDataInfo> infoLookUpMap = createlookupMap(mediainfos);

			List<MediaItem> updatedMedias = new ArrayList<MediaItem>();

			for (MediaItem mediaData : mediaService.getAllMediaData()) {
				if(infoLookUpMap.containsKey(mediaData.getFilehash()))
				{
					updateMedia(mediaData, infoLookUpMap.get(mediaData.getFilehash()));
					updatedMedias.add(mediaData);
				}
			}

			mediaService.update(updatedMedias);
		}
	}

	private TagLookUpCache taglookup;

	private void updateMedia(MediaItem media, MediaDataInfo newInfo){

		if(taglookup == null)
			taglookup = new TagLookUpCache();

		// update rating
		if(media.getRating() <= 0){
			media.setRating(newInfo.Rating);
		}

		if(media instanceof MovieMediaItem){
			MovieMediaItem movieMedia = (MovieMediaItem)media;
			if(movieMedia.getPreferredThumbPosition() == MovieMediaItem.INVALID_POSITION)
			{
				// only update the preferred position if this item does not have 
				// a such yet
				movieMedia.setPreferredThumbPosition(newInfo.ThumbPosition);
			}
		}

		// update tags
		List<Tag> potentialTags = new ArrayList<Tag>();

		for (String tagInfo : newInfo.Tags) {
			Tag tag = taglookup.findTagByName(tagInfo, /*search in keywords*/ true);

			if(tag == null && createMissingTags)
			{
				tag = taglookup.createTag(tagInfo);
			}

			if(tag != null)
				potentialTags.add(tag);
		}

		for (Tag tag : potentialTags) {
			media.addTag(tag);
		}
	}



	private Map<String, MediaDataInfo> createlookupMap(List<MediaDataInfo> mediainfos){
		Map<String, MediaDataInfo>  lookupMap = new HashMap<String, MediaDataInfo>();
		for (MediaDataInfo mediaDataInfo : mediainfos) { lookupMap.put(mediaDataInfo.File, mediaDataInfo); }
		return lookupMap;
	}


	private List<MediaDataInfo> parseJsonMediaInfo(String json){

		MediaDataInfoPack mediaDataInfos = null;

		Type collectionType = new TypeToken<MediaDataInfoPack>(){}.getType();

		Gson gson = new GsonBuilder().create();

		mediaDataInfos = gson.fromJson(json, collectionType);

		return mediaDataInfos.Medias;
	}

	private List<MediaDataInfo> generateDTOs(){
		List<MediaDataInfo> dtos = new ArrayList<MediaDataInfo>();

		for (MediaItem m : mediaService.getAllMediaData()) {

			MediaDataInfo mediaDataInfo = new MediaDataInfo(m);
			if(mediaDataInfo.hasInfo())
				dtos.add(mediaDataInfo);
		}
		return dtos;
	}



}
