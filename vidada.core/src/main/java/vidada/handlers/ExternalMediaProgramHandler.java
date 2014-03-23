package vidada.handlers;

import vidada.model.media.MediaItem;
import archimedesJ.io.ShellExec;
import archimedesJ.io.locations.ResourceLocation;

/**
 * This handler invokes an external program and passes the media URI and other parameters
 * 
 * @author IsNull
 *
 */
public abstract class ExternalMediaProgramHandler implements IMediaHandler {

	private final CommandTemplate template;
    private final  String name;
	/**
	 * Runs a dynamic command in the shell. 
	 * 
	 * 
	 * @param command 
	 */
	public ExternalMediaProgramHandler(String name, String command){
        this.name = name;
		this.template = new CommandTemplate(ShellExec.parseCommand(command, true));
	}


	@Override
	public final boolean handle(MediaItem media, ResourceLocation mediaResource) {
		String[] args = getCommandArgs(template, media, mediaResource);
		Process process = null;
		if(args != null){
			process = ShellExec.execute(args);
		}
		return process != null;
	}

    @Override
    public String getName(){
        return name;
    }


	/**
	 * Is this media supported by this external program?
	 * @param media
	 * @return
	 */
	protected abstract boolean isSupported(MediaItem media);

	/**
	 * Get the command line arguments to execute.
	 * @param command
	 * @param media
	 * @param mediaResource
	 * @return
	 */
	protected abstract String[] getCommandArgs(CommandTemplate command, MediaItem media, ResourceLocation mediaResource);


	/**
	 * Immutable Command template
	 * @author IsNull
	 *
	 */
	protected static class CommandTemplate{
		private final String[] parts;

		public CommandTemplate(String[] parts){
			this.parts = parts.clone();
		}

		/**
		 * Substitutes the given variable with the given value
		 * and returns a COPY of this template. 
		 * 
		 * This will not change the original!
		 * 
		 * @param variable
		 * @param value
		 * @return
		 */
		public CommandTemplate substitute(String variable, String value){
			String[] args = this.toArgs();
			for (int i = 0; i < args.length; i++) {
				args[i] = args[i].replace(variable, value);
			}
			return new CommandTemplate(args);
		}

		@Override
		public CommandTemplate clone(){
			return new CommandTemplate(this.parts);
		}

		public String[] toArgs(){
			return this.parts.clone();
		}
		
		@Override
		public String toString(){
			String str = "";
			for (String arg : parts) {
				str += arg + " ";
			}
			return str.trim();
		}
		
	}

}
