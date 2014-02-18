package vidada.dal;

import javax.persistence.EntityManager;

import vidada.dal.repositorys.CredentialRepository;
import vidada.dal.repositorys.DatabaseSettingsRepository;
import vidada.dal.repositorys.MediaLibraryRepository;
import vidada.dal.repositorys.MediaRepository;
import vidada.dal.repositorys.TagRepository;
import vidada.data.DatabaseConnectionException;
import vidada.server.repositories.ICredentialRepository;
import vidada.server.repositories.IDatabaseSettingsRepository;
import vidada.server.repositories.IMediaLibraryRepository;
import vidada.server.repositories.IMediaRepository;
import vidada.server.repositories.ITagRepository;
import vidada.server.repositories.RepositoryProvider;


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
