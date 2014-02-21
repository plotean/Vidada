package vidada.server.dal.repositories;

import vidada.server.settings.DatabaseSettings;

public interface IDatabaseSettingsRepository extends IRepository {
	/**
	 * Get the single entity. The entity will be cached in memory after first call.
	 * @return
	 */
	public abstract DatabaseSettings get();

	/**
	 * Store the entity
	 * @param item
	 */
	public abstract void store(DatabaseSettings newSettings);

	/**
	 * Update the entity
	 * @param item
	 */
	public abstract void update(DatabaseSettings settings);
}
