package vidada.handlers;

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
		CommandTemplate exec = command
				.substitute("$media", mediaResource.getPath());
		return exec.toArgs();		
	}

}
