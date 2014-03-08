package vidada.model.settings;

public class VidadaServer {
	private String uri;

	public VidadaServer(){}

	public VidadaServer(String uri){
		setUri(uri);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String location) {
		this.uri = location;
	}
}
