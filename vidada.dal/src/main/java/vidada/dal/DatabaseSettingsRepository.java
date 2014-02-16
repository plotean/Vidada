package vidada.dal;

import javax.persistence.TypedQuery;

import vidada.model.settings.DatabaseSettings;
import vidada.repositories.IDatabaseSettingsRepository;

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
