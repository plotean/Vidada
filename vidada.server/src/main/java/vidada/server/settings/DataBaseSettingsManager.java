package vidada.server.settings;

import java.util.concurrent.Callable;

import vidada.server.VidadaServer;
import vidada.server.dal.repositories.IDatabaseSettingsRepository;
import vidada.server.services.VidadaServerService;

public class DataBaseSettingsManager extends VidadaServerService implements IDatabaseSettingsService {

	public DataBaseSettingsManager(VidadaServer server) {
		super(server);
	}
	transient private final IDatabaseSettingsRepository repository = getRepository(IDatabaseSettingsRepository.class);


	/**
	 * Gets the application settings
	 * @return
	 */
	@Override
	public synchronized DatabaseSettings getSettings(){
		return runUnitOfWork(new Callable<DatabaseSettings>() {
			@Override
			public DatabaseSettings call() throws Exception {
				DatabaseSettings settings = repository.get(); 

				if(settings == null){
					settings = new DatabaseSettings();
					repository.store(settings);
				}
				return settings;
			}
		});
	}

	/**
	 * Persist the current changes
	 */
	@Override
	public void update(final DatabaseSettings settings){
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.update(settings);
			}
		});
	}
}
