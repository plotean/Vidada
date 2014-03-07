package vidada.model.update;

/**
 * Describes a state of the self updater
 * @author IsNull
 *
 */
public enum SelfUpdateState {

	Unknown,


	/**
	 * No update available, running latest version
	 */
	UpToDate,

	/**
	 * There is a newer version available for download
	 */
	UpdateAvailableForDownload,

	/**
	 * An update is currently downloading
	 */
	UpdateDownloading,

	/**
	 * An update was fetched and is ready to be installed.
	 */
	UpdateAvailableForInstall,

}
