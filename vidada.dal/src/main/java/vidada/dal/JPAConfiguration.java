package vidada.dal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAConfiguration {

	private EntityManagerFactory entityManagerFactory; 
	private EntityManager defaultEntityManager = null;

	private static JPAConfiguration instance;
	public synchronized static JPAConfiguration instance(){
		if(instance == null){
			instance = new JPAConfiguration();
		}
		return instance;
	}

	private JPAConfiguration(){
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

	public EntityManager getDefaultEntityManager(){
		if(defaultEntityManager == null){
			defaultEntityManager = getSessionFactory().createEntityManager();
		}
		return defaultEntityManager;
	}
}
