package vlc;

import archimedes.core.util.Lists;
import archimedes.core.util.OSValidator;
import archimedes.core.util.RegistryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * VLCUtil - Thread safe
 * @author IsNull
 *
 */
public class VLCUtil {

	/**
	 * Checks if VLC is installed
	 * @return
	 */
	public static boolean isVlcInstalled(){
		String vlc = getVLCBinaryPath();
		if(vlc != null){
			File libDir = new File(vlc);
			if(libDir.exists())
				return true;
		}
		return false;
	}


	public static String getVLCBinaryPath(){
		if(OSValidator.isWindows()){
			List<String> paths = getVLCInstallPathsWindows();
			if(!paths.isEmpty()){
				File vlcFolder = new File(paths.get(0));
				return (new File(vlcFolder, "vlc.exe")).toString();
			}
		}else if(OSValidator.isOSX()){
			return "/Applications/VLC.app/Contents/MacOS/VLC";
        }else if(OSValidator.isLinux() || OSValidator.isUnix()){
			return "/usr/bin/vlc";
		}
		return null;
	}

	public static String getVLCLibPath(){
		String path = null;
		List<String> vlcPaths = getVLCLibPaths();
		if(!vlcPaths.isEmpty())
			path = Lists.getFirst(vlcPaths);
		return path; 
	}



	public static List<String> getVLCLibPaths(){
		List<String> paths = new ArrayList<String>();

		//get the install directory of the VLC for windows
		if(OSValidator.isWindows()){
			paths.addAll(getVLCInstallPathsWindows());
		}else if(OSValidator.isOSX()){
			paths.add("/Applications/VLC.app/Contents/MacOS/lib");
		}else{
			// Linux / UNIX
			paths.add("/usr/lib");
			paths.add("/usr/lib/vlc");
		}
		return paths;
	}


	/**
	 * Searches the windows registry in order to find VLC install directory.
	 * Windows 32bit and 64bit registry paths will be searched.
	 * if one string is empty and the other one is not: return not empty string
	 * if both are empty: return null
	 * if both are not empty: return 32bit AND windows64 paths
	 * @return
	 */
	public static List<String> getVLCInstallPathsWindows(){

		List<String> paths = new ArrayList<String>();
		String windows64 = "";
		String windows32 = "";

		try {
			windows32 = RegistryUtil.readString (
					RegistryUtil.HKEY_LOCAL_MACHINE,				//HKEY
					"Software\\Wow6432Node\\VideoLan\\VLC",			//Key
					"InstallDir");									//ValueName			
		} catch (Exception e1) {
			e1.printStackTrace();
		}                   

		try {
			windows64 = RegistryUtil.readString (
					RegistryUtil.HKEY_LOCAL_MACHINE,				//HKEY
					"Software\\VideoLan\\VLC",						//Key
					"InstallDir");									//ValueName
		} catch (Exception e) {
			e.printStackTrace();
		}									

		if(windows64 != null)
			paths.add(windows64);
		if(windows32 != null)
			paths.add(windows32);

		return paths; 
	}

}
