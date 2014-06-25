package vidada.dal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class SessionManager
{
    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(SessionManager.class.getName());

    private EntityManagerFactory entityManagerFactory;

    /***************************************************************************
     *                                                                         *
     * Singleton                                                               *
     *                                                                         *
     **************************************************************************/

	private static SessionManager instance;
	public synchronized static SessionManager instance(){
		if(instance == null){
			instance = new SessionManager();
		}
		return instance;
	}

    /**
     * Private singleton constructor
     */
	private SessionManager(){
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	public synchronized EntityManagerFactory getSessionFactory(String dbpath){
		if(entityManagerFactory == null){

            logger.info("Creating EntityManagerFactory for db: " + dbpath);
			
			Map<String, String> config = new HashMap<>();
			config.put("hibernate.connection.url", "jdbc:h2:" + dbpath);
			config.put("hibernate.connection.username", "sa");
			config.put("hibernate.connection.password", "");

			entityManagerFactory = Persistence.createEntityManagerFactory( "manager", config );
		}
		return entityManagerFactory;
	}

}
