package vidada.data;
import java.io.File;
import java.net.URI;

import org.joda.time.base.AbstractDateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.base.BaseDateTime;

import vidada.model.settings.GlobalSettings;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.TNull;

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
			objectContainer = createEntityManager();
		}


		return objectContainer;
	}


	public static final int ACTIVATION_DEEPTH = 4;

	/**
	 * Creates a new entity manager
	 * @return
	 * @throws DatabaseConnectionException
	 */
	public static ObjectContainer createEntityManager() throws DatabaseConnectionException{
		//
		File dbPath = null;

		try{
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
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


			dbPath = GlobalSettings.getInstance().getAbsoluteDBPath();
			File dbFolder = dbPath.getParentFile();
			dbFolder.mkdirs();

			return Db4oEmbedded.openFile(
					config,
					dbPath.toString());
		}catch(Exception cause){
			throw new DatabaseConnectionException("Database connection to " +
					(dbPath != null ? dbPath.toString() : "<null>") +
					" could not be etablished.",
					cause);
		}
	}










}
