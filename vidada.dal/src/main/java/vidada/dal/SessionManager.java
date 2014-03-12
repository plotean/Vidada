package vidada.dal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SessionManager
{
	private EntityManagerFactory entityManagerFactory; 

	//private EntityManager defaultEntityManager = null;

	private static SessionManager instance;
	public synchronized static SessionManager instance(){
		if(instance == null){
			instance = new SessionManager();
		}
		return instance;
	}

	private SessionManager(){
		configure();
	}

	private void configure(){
		//configuration = new Configuration();
		//configuration.configure();
	}

	public synchronized EntityManagerFactory getSessionFactory(String dbpath){
		if(entityManagerFactory == null){
			
			System.out.println("SessionManager: Creating EntityManagerFactory for db: " + dbpath);
			
			Map<String, String> config = new HashMap<String, String>();
			config.put("hibernate.connection.url", "jdbc:h2:" + dbpath);
			config.put("hibernate.connection.username", "sa");
			config.put("hibernate.connection.password", "");

			entityManagerFactory = Persistence.createEntityManagerFactory( "manager", config );
		}
		return entityManagerFactory;
	}

}
