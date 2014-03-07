package vidada.model.update;

public class UpdateInformation {

	private String latestVersion;
	private boolean updateAvailable;

	public UpdateInformation(boolean updateAvailable, String latestVersion){
		this.updateAvailable = updateAvailable;
		this.latestVersion = latestVersion;
	}

	public boolean isUpdateAvailable(){
		return updateAvailable;
	}

	public String getLatestVersion(){
		return latestVersion;
	}
}
