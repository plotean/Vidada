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



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataBasePath == null) ? 0 : dataBasePath.hashCode());
		result = prime * result
				+ ((fileCachePath == null) ? 0 : fileCachePath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VidadaDatabase other = (VidadaDatabase) obj;
		if (dataBasePath == null) {
			if (other.dataBasePath != null)
				return false;
		} else if (!dataBasePath.equals(other.dataBasePath))
			return false;
		if (fileCachePath == null) {
			if (other.fileCachePath != null)
				return false;
		} else if (!fileCachePath.equals(other.fileCachePath))
			return false;
		return true;
	}


}
