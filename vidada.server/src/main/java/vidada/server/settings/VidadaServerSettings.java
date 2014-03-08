package vidada.server.settings;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.model.settings.JsonSettings;
import vidada.model.settings.VidadaDatabase;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.expressions.Predicate;
import archimedesJ.geometry.Size;
import archimedesJ.util.Lists;
import archimedesJ.util.OSValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VidadaServerSettings extends JsonSettings { 

	/***************************************************************************
	 *                                                                         *
	 * Transient fields                                                        *
	 *                                                                         *
	 **************************************************************************/

	transient public static final String ProductName = "vidada-server";

	transient public static File Path;
	transient public static String defaultCache; 
	transient public static String defaultDB;

	static {

		// Default paths

		Path = new File(".", ProductName + ".json");
		defaultCache = "data/cache"; 
		defaultDB  = "data/vidada.db";
	}


	transient private VidadaDatabase currentDBConfig = null;

	// thumb size boundaries
	transient public static final int THUMBNAIL_SIZE_MAX = 500;
	transient public static final int THUMBNAIL_SIZE_MIN = 100;
	transient public static final int THUMBNAIL_SIZE_GAP = 50;
	transient public static final double THUMBNAIL_SIDE_RATIO = 0.70;

	/***************************************************************************
	 *                                                                         *
	 * Persistent fields                                                       *
	 *                                                                         *
	 **************************************************************************/

	private Set<VidadaDatabase> databases = new HashSet<VidadaDatabase>();
	private boolean usingMetaData = true;
	private boolean isDebug = false;
	private boolean enableNetworkSharing = true;


	/***************************************************************************
	 *                                                                         *
	 * Singleton                                                               *
	 *                                                                         *
	 **************************************************************************/

	transient private static VidadaServerSettings instance; 
	public static VidadaServerSettings instance(){ 

		if(instance == null){

			if(Path.exists())
			{
				instance = JsonSettings.loadSettings(Path, VidadaServerSettings.class);
			}
		}

		if(instance == null){
			instance = new VidadaServerSettings();
		}

		instance.setPath(Path);
		instance.persist();

		return instance; 
	}

	private VidadaServerSettings(){
		databases.add(getDefaultConfig());
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Returns the max thumb resolution used in the whole application
	 * (HDPI aware)
	 * @return
	 */
	public Size getMaxThumbResolution(){
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
		return currentDBConfig;
	}

	private void checkDatabaseConfig() throws NotSupportedException{
		if(getCurrentDBConfig() == null)
		{
			throw new NotSupportedException("Database is not configured!");
		}
	}

	public void setCurrentDBConfig(VidadaDatabase db){
		currentDBConfig = db;
		System.out.println("current db: " + currentDBConfig != null ? currentDBConfig.getDataBasePath() : "null");
	}

	public File getAbsoluteDBPath(){
		checkDatabaseConfig();
		return toAbsolutePath(getCurrentDBConfig().getDataBasePath());
	}

	/**
	 * Gets the path to the local cache
	 * @return
	 */
	public File getAbsoluteCachePath() {
		checkDatabaseConfig();
		return toAbsolutePath(getCurrentDBConfig().getFileCachePath());
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

	public boolean isEnableNetworkSharing() {
		return enableNetworkSharing;
	}

	public void setEnableNetworkSharing(boolean enableNetworkSharing) {
		this.enableNetworkSharing = enableNetworkSharing;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private static methods                                                  *
	 *                                                                         *
	 **************************************************************************/

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


}
