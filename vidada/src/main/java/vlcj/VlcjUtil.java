package vlcj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import archimedesJ.util.Lists;
import archimedesJ.util.OSValidator;
import archimedesJ.util.RegistryUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * VlcjUtil - Thread safe
 * @author IsNull
 *
 */
public class VlcjUtil {

	static volatile boolean vlcjLoaded = false;
	static boolean vlcjLoadError = false;

	/**
	 * Checks if VLC is installed
	 * @return
	 */
	public static boolean isVlcInstalled(){
		List<String> vlcPaths = getVLCSearchPaths();
		for (String vlcPath : vlcPaths) {
			File libDir = new File(vlcPath);
			if(libDir.exists())
				return true;
		}
		return false;
	}


	/**
	 * Is the vlcj binding available?
	 * Requires that the native VLC libs are found and loaded by JNA
	 * 
	 * @return
	 */
	public synchronized static boolean isVlcjAvaiable(){
		ensureVLCLib();
		return vlcjLoaded;
	}


	/**
	 * Ensures that the vlcj Lib is properly loaded
	 */
	public synchronized static void ensureVLCLib(){

		if(!vlcjLoaded && !vlcjLoadError)
		{
			String vlclibName = RuntimeUtil.getLibVlcLibraryName();
			List<String> vlcPaths = getVLCSearchPaths();
			System.out.println("jni loading: " + vlclibName);
			
			
			for (String vlcPath : vlcPaths) {
				System.out.println("adding search path: " + vlcPath);
				NativeLibrary.addSearchPath(vlclibName, vlcPath);
			}

			try{
				Native.loadLibrary(vlclibName, LibVlc.class);
				vlcjLoaded = true;
			}catch(UnsatisfiedLinkError e){
				vlcjLoadError = true;
				e.printStackTrace();
			}
		}
	}

	public static String getVLCLibPath(){
		String path = null;
		List<String> vlcPaths = getVLCSearchPaths();
		if(!vlcPaths.isEmpty())
			path = Lists.getFirst(vlcPaths);
		return path; 
	}


	public static List<String> getVLCSearchPaths(){
		List<String> paths = new ArrayList<String>();
		
		//get the install directory of the VLC for windows
		if(OSValidator.isWindows()){
			paths.addAll(getVLCInstallPathsWindows());
		}else{
			paths.add("/Applications/VLC.app/Contents/MacOS/lib");
		}
		return paths;
	}


	/**
	 * Searches the windows registry in order to find VLC install directory.
	 * Windows 32bit and 64bit registry paths will be searched.
	 * if one string is empty and the other one is not: return not empty string
	 * if both are empty: return null
	 * if both are not empty: return windows64
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
