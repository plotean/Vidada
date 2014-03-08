package vidada.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Holds the applications global settings
 * @author IsNull
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VidadaClientSettings extends JsonSettings {

	/***************************************************************************
	 *                                                                         *
	 * Transient Fields                                                        *
	 *                                                                         *
	 **************************************************************************/

	transient public static final String ProductName = "vidada-client";

	transient public static File Path;
	transient public static String defaultCache; 
	transient public static File infoPropertiesFile; 

	static {
		// Default paths
		Path = new File(".", ProductName + ".json");
		defaultCache = "data/cache"; 
		infoPropertiesFile = new File(".", "info.properties");

	}

	transient private String versionInfo = null;
	transient private VidadaInstance currentInstance;

	/***************************************************************************
	 *                                                                         *
	 * Persisted Fields                                                        *
	 *                                                                         *
	 **************************************************************************/

	public Set<VidadaInstance> vidadaInstances = new HashSet<VidadaInstance>();
	private String localCachePath = defaultCache;
	private boolean enableDirectPlaySound = false;
	private boolean ignoreImages = false;
	private boolean ignoreMovies = false;
	private boolean forceHDPIRender = false;
	private boolean usingMetaData = true;
	private boolean isDebug = false;


	/***************************************************************************
	 *                                                                         *
	 * Singleton                                                               *
	 *                                                                         *
	 **************************************************************************/

	transient private static VidadaClientSettings instance; 
	public static VidadaClientSettings instance(){ 

		if(instance == null){
			if(Path.exists())
			{
				instance = JsonSettings.loadSettings(Path, VidadaClientSettings.class);
			}
		}

		if(instance == null){
			instance = new VidadaClientSettings();
		}

		instance.setPath(Path);
		instance.persist();

		return instance; 
	}

	private VidadaClientSettings(){ 
		vidadaInstances.add(VidadaInstance.LOCAL);
		vidadaInstances.add(new VidadaInstance("REST Localhost", "http://localhost:5555/api"));
	}


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Gets the path to the local cache
	 * @return
	 */
	public File getAbsoluteCachePath() {
		return toAbsolutePath(localCachePath);
	}



	public String getVersionInfo() {
		if(versionInfo == null)
		{
			Properties properties = new Properties();
			if(infoPropertiesFile.exists())
			{
				FileInputStream is = null;
				try {
					is = new FileInputStream(infoPropertiesFile);
					properties.load(is);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if(is != null)
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}else{
				System.err.println("can not find " + infoPropertiesFile.getAbsolutePath());
			}
			versionInfo = properties.getProperty("vidada.version", "unknown");
		}
		return versionInfo;
	}

	public boolean isForceHDPIRender() {
		return forceHDPIRender;
	}


	public void setForceHDPIRender(boolean forceHDPIRender) {
		this.forceHDPIRender = forceHDPIRender;
	}



	/**
	 * Is this app currently running in Debug mode?
	 * @return
	 */
	public boolean isDebug() {
		return isDebug;
	}

	/**
	 * Set the debug mode of this App
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * Should sound be played while DirectPlay preview?
	 * @return
	 */
	public boolean isEnableDirectPlaySound() {
		return enableDirectPlaySound;
	}

	/**
	 * Set if sound be played while DirectPlay preview.
	 * @param enableDirectPlaySound
	 */
	public void setEnableDirectPlaySound(boolean enableDirectPlaySound) {
		this.enableDirectPlaySound = enableDirectPlaySound;
	}


	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages) {
		this.ignoreImages = ignoreImages;
	}

	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public void setIgnoreMovies(boolean ignoreMovies) {
		this.ignoreMovies = ignoreMovies;
	}

	public boolean isUsingMetaData() {
		return usingMetaData;
	}

	public void setUsingMetaData(boolean usingMetaData) {
		this.usingMetaData = usingMetaData;
	}

	public Collection<VidadaInstance> getVidadaInstances(){
		return vidadaInstances;
	}


	/***************************************************************************
	 *                                                                         *
	 * Transient Properties                                                    *
	 *                                                                         *
	 **************************************************************************/

	public void setCurrentInstnace(VidadaInstance instance) {
		currentInstance = instance;
	}

	public VidadaInstance getCurrentInstance(){
		return currentInstance;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private static methods                                                  *
	 *                                                                         *
	 **************************************************************************/


	private static File toAbsolutePath(String path){
		return toAbsolutePath(new File(path));
	}

	private static File toAbsolutePath(File path){
		if(path == null) return null;

		if(path.isAbsolute())
		{
			return path;
		}else {
			return new File(".", path.getPath());
		}
	}



}


