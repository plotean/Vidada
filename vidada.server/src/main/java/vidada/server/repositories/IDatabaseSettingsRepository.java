package vidada.server.repositories;

import vidada.server.settings.DatabaseSettings;

public interface IDatabaseSettingsRepository extends IRepository<DatabaseSettings> {
	/**
	 * Get the single entity. The entity will be cached in memory after first call.
	 * @return
	 */
	public abstract DatabaseSettings get();

	/**
	 * Update the entity
	 * @param item
	 */
	public abstract void update(DatabaseSettings settings);
}
