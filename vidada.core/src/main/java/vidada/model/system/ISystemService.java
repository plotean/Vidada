package vidada.model.system;

import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.services.IService;

/**
 * Provides platform specific general file explorer methods
 * such as open a file by the default program or show a folder
 * 
 * @author IsNull
 *
 */
public interface ISystemService extends IService{

	/**
	 * Open this resource with the standard tool registered in the current Operating System
	 * @param resource
	 * @return Returns true upon success
	 */
	public abstract boolean open(ResourceLocation resource);

	/**
	 * Show the given resource location. 
	 * 
	 * For a file in a local or remote directory,
	 * this usually shows a browser window whit the file selected in it.
	 *  
	 * @param resource
	 */
	public abstract void showResourceHome(ResourceLocation resource);
}
