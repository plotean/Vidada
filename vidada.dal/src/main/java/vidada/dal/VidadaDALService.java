package vidada.dal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

import vidada.aop.IUnitOfWorkRunner;
import vidada.aop.IUnitOfWorkService;
import vidada.aop.UnitOfWorkService;
import vidada.aop.UnitOfWorkService.IUnitOfWorkIntercepter;
import vidada.dal.repositorys.CredentialRepository;
import vidada.dal.repositorys.DatabaseSettingsRepository;
import vidada.dal.repositorys.MediaLibraryRepository;
import vidada.dal.repositorys.MediaRepository;
import vidada.dal.repositorys.TagRepository;
import vidada.server.dal.IVidadaDALService;
import vidada.server.dal.repositories.ICredentialRepository;
import vidada.server.dal.repositories.IDatabaseSettingsRepository;
import vidada.server.dal.repositories.IMediaLibraryRepository;
import vidada.server.dal.repositories.IMediaRepository;
import vidada.server.dal.repositories.IRepository;
import vidada.server.dal.repositories.ITagRepository;

/**
 * Implementation of the Vidada DAL service for JPA 
 * @author IsNull
 *
 */
class VidadaDALServiceJPA implements IVidadaDALService {

	private final RepositoryManager repositoryManager = new RepositoryManager();
	private final IUnitOfWorkService<EntityManager> unitOfWorkService = new UnitOfWorkService<EntityManager>();

	public VidadaDALServiceJPA(final EntityManagerFactory entityManagerFactory){

		registerRepos();

		unitOfWorkService.setIntercepter(new IUnitOfWorkIntercepter<EntityManager>() {
			@Override
			public EntityManager initUnitOfWork() {
				EntityManager em;
				em = entityManagerFactory.createEntityManager();
				em.setFlushMode(FlushModeType.AUTO);
				em.getTransaction().begin();

				System.out.println(">>>UNIT OF WORK START!");
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
						System.out.println("Rolling back transaction...");
						em.getTransaction().rollback();
					}
				}finally{
					em.close();
				}

				System.out.println("<<<UNIT OF WORK END!");
			}
		});
	}

	private void registerRepos(){
		repositoryManager.register(IMediaLibraryRepository.class, new MediaLibraryRepository(unitOfWorkService));
		repositoryManager.register(ITagRepository.class, new TagRepository(unitOfWorkService));
		repositoryManager.register(IDatabaseSettingsRepository.class, new DatabaseSettingsRepository(unitOfWorkService));
		repositoryManager.register(IMediaRepository.class, new MediaRepository(unitOfWorkService));
		repositoryManager.register(ICredentialRepository.class, new CredentialRepository(unitOfWorkService));
	}


	@Override
	public <T extends IRepository> T getRepository(Class<T> iclazz) {
		return repositoryManager.resolve(iclazz);
	}

	@Override
	public IUnitOfWorkRunner getUnitOfWorkRunner() {
		return unitOfWorkService;
	}

}
