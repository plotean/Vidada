package vidada.model.settings;

public class VidadaDatabase {

	private String fileCachePath;
	private String dataBasePath;

	public VidadaDatabase(){}

	public VidadaDatabase(String dataBasePath, String fileCachePath){
		this.dataBasePath = dataBasePath;
		this.fileCachePath = fileCachePath;
	}

	public String getFileCachePath() {
		return fileCachePath;
	}
	public void setFileCachePath(String fileCachePath) {
		this.fileCachePath = fileCachePath;
	}
	public String getDataBasePath() {
		return dataBasePath;
	}
	public void setDataBasePath(String dataBasePath) {
		this.dataBasePath = dataBasePath;
	}

	@Override
	public String toString(){
		return getDataBasePath();
	}

}
