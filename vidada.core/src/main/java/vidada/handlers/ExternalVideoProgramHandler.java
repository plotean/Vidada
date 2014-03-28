package vidada.handlers;

import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;


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
     * @param name Name of this media handler
	 * @param command 
	 */
	public ExternalVideoProgramHandler(String name, String command) {
		super(name, command);
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
