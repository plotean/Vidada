package vidada.server.settings;

public interface IDatabaseSettingsService {

	DatabaseSettings getSettings();

	void update(DatabaseSettings settings);

}
