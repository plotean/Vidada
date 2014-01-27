package vidada.model.media.store;

import java.net.URI;

import vidada.model.entities.BaseEntity;

/**
 * Represents a simple URI to a media store
 * @author IsNull
 *
 */
public class StoreLocation extends BaseEntity {
	private URI uri;
	// TODO Credentials?

	public StoreLocation(URI storeUri) {
		setUri(storeUri);
	}

	public URI getUri() {
		return uri;
	}

	protected void setUri(URI storeUri) {
		this.uri = storeUri;
	}
}
