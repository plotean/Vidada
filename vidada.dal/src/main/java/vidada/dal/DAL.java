package vidada.dal;

import javax.persistence.EntityManagerFactory;

import vidada.data.DatabaseConnectionException;
import vidada.server.dal.IVidadaDALService;


public class DAL {

	/**
	 * Build the DAL service
	 * @return
	 */
	public static IVidadaDALService build(){
		try{
			EntityManagerFactory emFactory = SessionManager.instance().getSessionFactory();
			IVidadaDALService vidadaDALService = new VidadaDALServiceJPA(emFactory);

			return vidadaDALService;
		}catch(Throwable e){
			e.printStackTrace();
			throw new DatabaseConnectionException("DAL failed", e);
		}	
	}

}
