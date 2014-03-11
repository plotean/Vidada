package vidada.handlers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import archimedesJ.io.locations.ResourceLocation;


public class ExternalVideoProgramHandler extends ExternalMediaProgramHandler {

	/**
	 * Runs a dynamic command in the shell. 
	 * 
	 * Supported parameters:
	 * 
	 * $media
	 * $media_escaped
	 * 
	 * Examples:
	 * 
	 * C:\vlc.exe $media
	 * 
	 * /applications/vlc $media
	 * 
	 * @param command 
	 */
	public ExternalVideoProgramHandler(String command) {
		super(command);
	}

	@Override
	protected boolean isSupported(MediaItem media) {
		return media != null && media.getType().equals(MediaType.MOVIE);
	}

	@Override
	protected String[] getCommandArgs(CommandTemplate command, MediaItem media, ResourceLocation mediaResource) {
		String mediaPath =	getMediaPath(mediaResource);
		return command.substitute("$media", mediaPath).toArgs();
	}

	/**
	 * Returns the media path. This will remove the URI parts if it is a local file.
	 * In any other case, this will return the normal decoded URL
	 * @param mediaResource
	 * @return
	 */
	private String getMediaPath(ResourceLocation mediaResource){
		String mediaPath = "";
		try {
			URI resource = mediaResource.getUri();
			if("file".equals(resource.getScheme())){
				mediaPath = URLDecoder.decode(resource.toString(), "utf-8");
				mediaPath = new File(mediaPath).getPath();
				mediaPath = mediaPath.replace("file:", "");
			}else{
				mediaPath = mediaResource.getUriString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return mediaPath;
	}
}
