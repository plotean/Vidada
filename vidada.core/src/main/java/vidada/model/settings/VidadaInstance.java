package vidada.model.settings;

/**
 * This is a helper class for Vidada settings and represents a instance.
 * A Vidada Instance can be a local database or a remote Vidada server to connect to.
 */
public class VidadaInstance {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

	transient public static VidadaInstance LOCAL = new VidadaInstance("Local Instance", "local");

	private String name;
	private String uri;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	public VidadaInstance(){}

	public VidadaInstance(String name, String uri){
		setName(name);
		setUri(uri);
	}

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

	public String getUri() {
		return uri;
	}

	public void setUri(String location) {
		this.uri = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    /***************************************************************************
     *                                                                         *
     * Overridden Public API                                                   *
     *                                                                         *
     **************************************************************************/

	@Override
	public String toString(){
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		VidadaInstance other = (VidadaInstance) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}


}
