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
			objectContainer = createEntityManager();
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

			/*
			// fix for db4o on Android
			// Generally enums can not be stored: 
			// TODO: (maybe we could even hack a generic converter...)
			config.common().objectClass(java.lang.Enum.class).translate(new TLoggerProxy());
			// Enums which shall be storeable must have a converter 
			config.common().objectClass(MediaType.class).translate(new TEnumMediaType());


			config.common().registerTypeHandler(new TypeHandlerPredicate() {

				@Override
				public boolean match(ReflectClass arg0) {
					System.err.println("ReflectClass: " + arg0.getName());
					return false;
				}
			}, new TypeHandler4() {
				@Override
				public void write(WriteContext writeCtx, Object arg1) { }

				@Override
				public void delete(DeleteContext delereCtx) throws Db4oIOException { }

				@Override
				public void defragment(DefragmentContext defragCtx) { }
			});
			 */


			// generate world wide unique uuids for each stored object
			// config.file().generateUUIDs(ConfigScope.GLOBALLY);


			dbPath = GlobalSettings.getInstance().getAbsoluteDBPath();
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
