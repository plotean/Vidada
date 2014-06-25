package vidada.dal;

import archimedes.core.aop.IUnitOfWorkRunner;
import archimedes.core.aop.IUnitOfWorkService;
import archimedes.core.aop.UnitOfWorkService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.dal.repositorys.*;
import vidada.server.dal.IVidadaDALService;
import vidada.server.dal.repositories.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

/**
 * Implementation of the Vidada DAL service for JPA 
 * @author IsNull
 *
 */
class VidadaDALServiceJPA implements IVidadaDALService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(VidadaDALServiceJPA.class.getName());

	private final RepositoryManager repositoryManager = new RepositoryManager();
	private final IUnitOfWorkService<EntityManager> unitOfWorkService = new UnitOfWorkService<>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	public VidadaDALServiceJPA(final EntityManagerFactory entityManagerFactory){

		registerRepos();

		unitOfWorkService.setIntercepter(new UnitOfWorkService.IUnitOfWorkInterceptor<EntityManager>() {
			@Override
			public EntityManager initUnitOfWork() {
				EntityManager em;
				em = entityManagerFactory.createEntityManager();
				em.setFlushMode(FlushModeType.AUTO);
				em.getTransaction().begin();

                logger.info(">>>UNIT OF WORK START!");
				return em;
			}

			@Override
			public void finalizeUnitOfWork(EntityManager em) {
				try{
					em.flush();
					em.getTransaction().commit();
				}catch(Exception e){
					e.printStackTrace();
					if(em.getTransaction().isActive()){
                        logger.info("Rolling back transaction...");
						em.getTransaction().rollback();
					}
				}finally{
					em.close();
				}

                logger.info("<<<UNIT OF WORK END!");
			}
		});
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    @Override
    public <T extends IRepository> T getRepository(Class<T> iclazz) {
        return repositoryManager.resolve(iclazz);
    }

    @Override
    public IUnitOfWorkRunner getUnitOfWorkRunner() {
        return unitOfWorkService;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private void registerRepos(){
        repositoryManager.register(IMediaLibraryRepository.class, new MediaLibraryRepository(unitOfWorkService));
        repositoryManager.register(ITagRepository.class, new TagRepository(unitOfWorkService));
        repositoryManager.register(IDatabaseSettingsRepository.class, new DatabaseSettingsRepository(unitOfWorkService));
        repositoryManager.register(IMediaRepository.class, new MediaRepository(unitOfWorkService));
        repositoryManager.register(ICredentialRepository.class, new CredentialRepository(unitOfWorkService));
    }

}
