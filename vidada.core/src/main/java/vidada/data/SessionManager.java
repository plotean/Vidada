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


	public static ObjectContainer getObjectContainer() throws EMFactoryCreationException {

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
	 * @throws EMFactoryCreationException
	 */
	public static ObjectContainer createEntityManager() throws EMFactoryCreationException{
		//

		/**/
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().callConstructors(true);
		config.common().updateDepth(2);


		config.common().objectClass(org.joda.time.DateTime.class).translate(new TJodaDateTime ());
		config.common().objectClass(BaseDateTime.class).translate(new TNull()); 
		config.common().objectClass(AbstractDateTime.class).translate(new TNull()); 
		config.common().objectClass(AbstractInstant.class).translate(new TNull()); 

		config.common().objectClass(File.class).translate(new TFile());
		config.common().objectClass(URI.class).translate(new TURI());

		return Db4oEmbedded.openFile(
				config,
				GlobalSettings.getInstance().getAbsoluteDBPath().toString());


	}










}
