package vidada.dal.repositorys;

import javax.persistence.TypedQuery;

import vidada.dal.JPARepository;
import vidada.server.repositories.IDatabaseSettingsRepository;
import vidada.server.settings.DatabaseSettings;

public class DatabaseSettingsRepository extends JPARepository implements IDatabaseSettingsRepository {

	@Override
	public DatabaseSettings get() {
		TypedQuery<DatabaseSettings> query = getEntityManager().createQuery("SELECT s FROM DatabaseSettings s", DatabaseSettings.class);
		return firstOrDefault(query);
	}

	@Override
	public void update(DatabaseSettings settings) {
		getEntityManager().persist(settings);
	}

}
