package vidada.dal.repositorys;

import archimedes.core.aop.IUnitOfWorkService;
import vidada.dal.JPARepository;
import vidada.server.dal.repositories.IDatabaseSettingsRepository;
import vidada.server.settings.DatabaseSettings;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class DatabaseSettingsRepository extends JPARepository implements IDatabaseSettingsRepository {

	public DatabaseSettingsRepository(
			IUnitOfWorkService<EntityManager> unitOfWorkService) {
		super(unitOfWorkService);
	}

	@Override
	public DatabaseSettings get() {
		TypedQuery<DatabaseSettings> query = getEntityManager().createQuery("SELECT s FROM DatabaseSettings s", DatabaseSettings.class);
		return firstOrDefault(query);
	}

	@Override
	public void update(DatabaseSettings settings) {
		getEntityManager().merge(settings);
	}

	@Override
	public void store(DatabaseSettings newSettings) {
		getEntityManager().persist(newSettings);
	}

}
