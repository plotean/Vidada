package vidada.data;
import java.io.File;
import java.net.URI;

import org.joda.time.base.AbstractDateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.base.BaseDateTime;

import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.settings.GlobalSettings;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.TNull;
import com.db4o.ext.StoredClass;

import db4o.translators.TFile;
import db4o.translators.TJodaDateTime;
import db4o.translators.TURI;

/**
 * Manages the persistence sessions
 * 
 * @author IsNull
 *
 */
public class SessionManager {


	private static ObjectContainer objectContainer;


	public static ObjectContainer getObjectContainer() throws DatabaseConnectionException {

		if(objectContainer == null)
		{
			objectContainer = createObjectContainer();
			printDBClasses(objectContainer);

		}

		return objectContainer;
	}


	private static void printDBClasses(ObjectContainer container) {
		System.out.println("listing all stored classes in DB:");

		StoredClass[] classesInDB = container.ext().storedClasses();
		for (StoredClass storedClass : classesInDB) {
			StoredClass parent = storedClass.getParentStoredClass();
			String parentName = parent != null ? " <-- " + parent.getName() : "";
			System.out.println("class: " + storedClass.getName() + parentName);
		}
	}


	public static final int ACTIVATION_DEEPTH = 4;

	/**
	 * Creates a new object container for the default Vidada DB
	 * @return
	 * @throws DatabaseConnectionException
	 */
	public static ObjectContainer createObjectContainer() throws DatabaseConnectionException{
		File dbPath = GlobalSettings.getInstance().getAbsoluteDBPath();
		return createObjectContainer(dbPath);
	}



	/**
	 * Creates a new object container for the given db file.
	 * If a file does not exists, a new DB will be generated there.
	 * 
	 * @return
	 * @throws DatabaseConnectionException
	 */
	public static ObjectContainer createObjectContainer(final File dbPath) throws DatabaseConnectionException{

		try{
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();

			config.file().storage(new EncryptedStorage("vidada"));

			System.out.println("storage type: " + config.file().storage());

			config.common().callConstructors(true);
			config.common().updateDepth(2);


			config.common().objectClass(org.joda.time.DateTime.class).translate(new TJodaDateTime ());
			config.common().objectClass(BaseDateTime.class).translate(new TNull()); 
			config.common().objectClass(AbstractDateTime.class).translate(new TNull()); 
			config.common().objectClass(AbstractInstant.class).translate(new TNull()); 
			config.common().objectClass(File.class).translate(new TFile());
			config.common().objectClass(URI.class).translate(new TURI());

			// generate world wide unique uuids for each stored object
			// config.file().generateUUIDs(ConfigScope.GLOBALLY);

			// cascade delte config
			config.common().objectClass(MediaItem.class).objectField("sources").cascadeOnDelete(true);
			config.common().objectClass(MediaSourceLocal.class).objectField("relativeFilePath").cascadeOnDelete(true);
			config.common().objectClass(MediaLibrary.class).objectField("libraryEntries").cascadeOnDelete(true);


			// ensure that the path (folders) exist
			File dbFolder = dbPath.getParentFile();
			dbFolder.mkdirs();

			return Db4oEmbedded.openFile(
					config,
					dbPath.toString());
		}catch(Exception cause){
			cause.printStackTrace();

			throw new DatabaseConnectionException("Database connection to " +
					(dbPath != null ? dbPath.toString() : "<null>") +
					" could not be etablished.",
					cause);
		}
	}






}
