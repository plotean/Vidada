package vidada.dal;

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

	public synchronized EntityManagerFactory getSessionFactory(){
		if(entityManagerFactory == null){
			entityManagerFactory = Persistence.createEntityManagerFactory( "manager" );
		}
		return entityManagerFactory;
	}

}
