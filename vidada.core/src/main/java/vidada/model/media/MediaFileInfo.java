package vidada.model.media;

import java.util.HashMap;
import java.util.Map;

import archimedesJ.io.locations.ILocationFilter;
import archimedesJ.io.locations.LocationFilters;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.Lists;

public class MediaFileInfo {
	private MediaType type;
	private String[] fileExtensions;
	transient private ILocationFilter fileFilter;

	public MediaFileInfo(MediaType type, String[] fileExtensions){
		this.type = type;
		this.fileExtensions = fileExtensions;
	}

	public boolean isFileofThisType(ResourceLocation file){
		return getPathFilter().accept(file);
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public String[] getFileExtensions() {
		return fileExtensions;
	}

	public void setFileExtensions(String[] fileExtensions) {
		this.fileExtensions = fileExtensions;
		fileFilter = LocationFilters.extensionFilter(fileExtensions);
	}

	public ILocationFilter getPathFilter() {
		if(fileFilter == null)
			fileFilter =  LocationFilters.extensionFilter(fileExtensions); // PathSupport.buildExtensionFilter(fileExtensions);
		return fileFilter;
	}

	public static MediaFileInfo get(MediaType type){
		return mediaMap.get(type);
	}

	private final static Map<MediaType, MediaFileInfo> mediaMap = new HashMap<MediaType, MediaFileInfo>();

	static{
		//todo load from config files...
		String[] knownMovieExtensions = {".avi", ".aaf", ".3gp", ".wmv",".divx", ".asf" ,".mkv", ".vlc", ".mp4",".mpg",".mpeg",".m4v",".mov", ".rm" ,".rmvb", ".flv", ".mts"};
		String[] knownImageExtensions = {".png", ".bmp", ".jpg", ".jpeg", ".gif"};

		mediaMap.put(MediaType.MOVIE, new MediaFileInfo(MediaType.MOVIE, knownMovieExtensions));
		mediaMap.put(MediaType.IMAGE, new MediaFileInfo(MediaType.IMAGE, knownImageExtensions));
	}

	public static MediaFileInfo[] getKnownMediaInfos(){

		MediaFileInfo[] infos = new  MediaFileInfo[]{
				mediaMap.get(MediaType.MOVIE),
				mediaMap.get(MediaType.IMAGE),
		};

		return infos;
	}

	/**
	 * Gets all supported Media Extensions
	 * @return
	 */
	public static String[] getAllMediaExtensions(){
		String[] allMediaExtensions = new String[0];

		for (MediaFileInfo mediaInfo : getKnownMediaInfos()) {
			allMediaExtensions = Lists.concat(allMediaExtensions, mediaInfo.getFileExtensions());
		}

		return allMediaExtensions;
	}


	private static String[] getAllMediaExtensionsDotLess(){
		String[] allMediaExtensions = new String[0];

		for (MediaFileInfo mediaInfo : getKnownMediaInfos()) {
			allMediaExtensions = Lists.concat(allMediaExtensions, mediaInfo.getFileExtensions());
		}

		for (int i = 0; i < allMediaExtensions.length; i++) {
			allMediaExtensions[i] = allMediaExtensions[i].substring(1);
		}

		return allMediaExtensions;
	}


}
