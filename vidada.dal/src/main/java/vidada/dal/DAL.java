package vidada.dal;

import java.io.File;

import javax.persistence.EntityManagerFactory;

import vidada.data.DatabaseConnectionException;
import vidada.server.dal.IVidadaDALService;

/**
 * Vidada Data Abstraction Layer
 * 
 * @author IsNull
 *
 */
public class DAL {


	/**
	 * Build a DAL service
	 * @param db Database location
	 * @return
	 */
	public static IVidadaDALService build(File db){
		try{
			EntityManagerFactory emFactory = SessionManager.instance().getSessionFactory(db.getAbsolutePath());
			IVidadaDALService vidadaDALService = new VidadaDALServiceJPA(emFactory);
			return vidadaDALService;
		}catch(Throwable e){
			e.printStackTrace();
			throw new DatabaseConnectionException("DAL failed", e);
		}	
	}
}
