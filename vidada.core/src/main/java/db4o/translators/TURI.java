package db4o.translators;

import java.net.URI;
import java.net.URISyntaxException;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

public class TURI implements ObjectConstructor{

	@Override
	public Object onInstantiate(ObjectContainer container, Object storedObject) {
		String path = (String)storedObject;
		try {
			return path != null ? new URI(path) : null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object onStore(ObjectContainer container, Object applicationObject) {
		if(applicationObject instanceof URI){
			URI file = (URI)applicationObject;
			return file.toString();
		}
		return null;
	}

	@Override
	public void onActivate(ObjectContainer container,
			Object applicationObject,
			Object storedObject) {
		// do nothing
	}

	@Override
	public Class storedClass() {
		return String.class;
	}

}
