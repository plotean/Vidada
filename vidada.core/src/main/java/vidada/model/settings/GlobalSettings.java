package vidada.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.expressions.Predicate;
import archimedesJ.geometry.Size;
import archimedesJ.util.Lists;
import archimedesJ.util.OSValidator;

import com.google.gson.reflect.TypeToken;

/**
 * Holds the applications global settings
 * @author IsNull
 *
 */
public class GlobalSettings extends JsonSettings {

	// transient fields
	transient public static final String ProductName = "Vidada";

	transient public static File Path;
	transient public static String defaultCache; 
	transient public static String defaultDB;
	transient public static File infoPropertiesFile; 

	static {

		// Default paths

		Path = new File(".", ProductName + ".json");
		defaultCache = "data/cache"; 
		defaultDB  = "data/vidada.db";
		infoPropertiesFile = new File(".", "info.properties");

	}




	transient private String versionInfo = null;


	transient private VidadaDatabase currentDBConfig = null;

	// thumb size boundaries
	transient public static final int THUMBNAIL_SIZE_MAX = 500;
	transient public static final int THUMBNAIL_SIZE_MIN = 100;
	transient public static final int THUMBNAIL_SIZE_GAP = 50;
	transient public static final double THUMBNAIL_SIDE_RATIO = 0.70;


	// persistent fields
	private List<VidadaDatabase> databases = new ArrayList<VidadaDatabase>();
	private boolean forceHDPIRender = false;
	private boolean usingMetaData = true;
	private boolean isDebug = false;


	/**
	 * Returns the max thumb resolution
	 * (HDPI aware)
	 * @return
	 */
	public static Size getMaxThumbResolution(){
		int maxWidth = THUMBNAIL_SIZE_MAX * (OSValidator.isHDPI() ? 2 : 1);
		return new Size(
				maxWidth,
				(int)((double)maxWidth*THUMBNAIL_SIDE_RATIO));
	}




	/**
	 * 
	 * @return
	 */
	public List<VidadaDatabase> getAvaiableDatabases(){
		return Lists.where(databases, new Predicate<VidadaDatabase>() {
			@Override
			public boolean where(VidadaDatabase t) { 
				File path = toAbsolutePath(t.getDataBasePath()).getParentFile().getAbsoluteFile();

				System.out.println(path.getAbsolutePath() + " -- " + path.exists());
				return path.exists(); 
			}
		});
	}

	/**
	 * Automatically configure the database
	 * @return Returns true if the db was configured successfully.
	 */
	public boolean autoConfigDatabase() {

		if(OSValidator.isAndroid())
		{
			System.out.println("config db for Android, using default location:");
			setCurrentDBConfig(getDefaultConfig());
			return true;
		}else{
			System.out.println("found " + databases.size() + " configured DBs.");

			List<VidadaDatabase> availableDbs = getAvaiableDatabases();

			if(availableDbs.isEmpty()){
				System.out.println("No database config points to an existing location. Restored defaults.");
				databases.add(getDefaultConfig());
				availableDbs.add(getDefaultConfig());
				persist();
			}

			if(availableDbs.size() == 1){
				VidadaDatabase mydb = availableDbs.get(0);
				if(mydb != null)
				{
					setCurrentDBConfig(mydb);
					System.out.println("auto config the only database as primary");
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * Available
	 * @return
	 */
	public VidadaDatabase getCurrentDBConfig() {
		if(currentDBConfig == null)
		{
			throw new NotSupportedException("no currentDBConfig available!");
		}
		return currentDBConfig;
	}

	public void setCurrentDBConfig(VidadaDatabase db){
		currentDBConfig = db;
		System.out.println("current db: " + currentDBConfig.getDataBasePath());
	}

	public File getAbsoluteDBPath(){
		return toAbsolutePath(getCurrentDBConfig().getDataBasePath());
	}

	public File getAbsoluteCachePath() {
		return toAbsolutePath(getCurrentDBConfig().getFileCachePath());
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


	//
	// Singleton --Settings construction
	//
	transient private static GlobalSettings instance; 
	public static GlobalSettings getInstance(){ 

		if(instance == null){

			if(Path.exists())
			{
				Type settingsType = new TypeToken<GlobalSettings>(){}.getType();
				Object settingsObj = JsonSettings.loadSettings(Path, settingsType);
				if(settingsObj instanceof GlobalSettings)
				{
					instance = (GlobalSettings)settingsObj;
				}
			}
		}

		if(instance == null){
			instance = new GlobalSettings();
		}

		instance.setPath(Path);
		instance.persist();

		return instance; 
	}
	private GlobalSettings(){
		databases.add(getDefaultConfig());
	}

	private static VidadaDatabase getDefaultConfig(){
		VidadaDatabase defaultConfig = new VidadaDatabase();
		defaultConfig.setDataBasePath(defaultDB);
		defaultConfig.setFileCachePath(defaultCache);
		return defaultConfig;
	}


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


	public boolean isForceHDPIRender() {
		return forceHDPIRender;
	}


	public void setForceHDPIRender(boolean forceHDPIRender) {
		this.forceHDPIRender = forceHDPIRender;
	}

	/**
	 * Determites if meta data is used to help identify files 
	 * and store additional informations
	 * @return
	 */
	public boolean isUsingMetaData() {
		return usingMetaData;
	}


	public void setUsingMetaData(boolean usingMetaData) {
		this.usingMetaData = usingMetaData;
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
}


