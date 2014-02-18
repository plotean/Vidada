package vidada.dal;

import javax.persistence.EntityManager;

import vidada.dal.repositorys.CredentialRepository;
import vidada.dal.repositorys.DatabaseSettingsRepository;
import vidada.dal.repositorys.MediaLibraryRepository;
import vidada.dal.repositorys.MediaRepository;
import vidada.dal.repositorys.TagRepository;
import vidada.data.DatabaseConnectionException;
import vidada.repositories.ICredentialRepository;
import vidada.repositories.IDatabaseSettingsRepository;
import vidada.repositories.IMediaLibraryRepository;
import vidada.repositories.IMediaRepository;
import vidada.repositories.ITagRepository;
import vidada.repositories.RepositoryProvider;


public class DAL {

	public static void activate(){
		try{
			EntityManager em = JPAConfiguration.instance().getDefaultEntityManager();
			registerRepositories();
		}catch(Throwable e){
			e.printStackTrace();
			throw new DatabaseConnectionException("DAL failed", e);
		}
		//provider.register(ITagRepository.class, TagRepository.class);
	}

	private static void registerRepositories(){
		RepositoryProvider provider = RepositoryProvider.getInstance();
		provider.register(IMediaLibraryRepository.class, MediaLibraryRepository.class);
		provider.register(ITagRepository.class, TagRepository.class);
		provider.register(IDatabaseSettingsRepository.class, DatabaseSettingsRepository.class);
		provider.register(IMediaRepository.class, MediaRepository.class);
		provider.register(ICredentialRepository.class, CredentialRepository.class);

	}
}
