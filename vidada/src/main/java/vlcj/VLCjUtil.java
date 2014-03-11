package vlcj;

import java.util.List;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vlc.VLCUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * Utility for VLCj which helps loading the JNI libraries.
 * @author IsNull
 *
 */
public class VLCjUtil {

	static volatile boolean vlcjLoaded = false;
	static boolean vlcjLoadError = false;


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
			List<String> libPaths = VLCUtil.getVLCLibPaths();
			System.out.println("jni loading: " + vlclibName);


			for (String libPath : libPaths) {
				System.out.println("adding search path: " + libPath);
				NativeLibrary.addSearchPath(vlclibName, libPath);
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

}
