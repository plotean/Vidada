package vidada.server.settings;

import vidada.server.repositories.IDatabaseSettingsRepository;
import vidada.server.repositories.RepositoryProvider;

public class DataBaseSettingsManager {

	transient private final static IDatabaseSettingsRepository repository = RepositoryProvider.Resolve(IDatabaseSettingsRepository.class);

	private static DatabaseSettings cacheDatabaseSettings;

	/**
	 * Gets the application settings
	 * @return
	 */
	public synchronized static DatabaseSettings getSettings(){
		if(cacheDatabaseSettings == null){
			DatabaseSettings settings = repository.get(); 
			if(settings == null){
				settings = new DatabaseSettings();
				persist(settings);
			}
			cacheDatabaseSettings = settings;
		}
		return cacheDatabaseSettings;
	}

	/**
	 * Persist the current changes
	 */
	public static void persist(DatabaseSettings settings){
		repository.update(settings);
	}
}
