package db4o.translators;

import java.io.File;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

public class TFile implements ObjectConstructor{

	@Override
	public Object onInstantiate(ObjectContainer container, Object storedObject) {
		String path = (String)storedObject;
		return path != null ? new File(path) : null;
	}

	@Override
	public Object onStore(ObjectContainer container, Object applicationObject) {
		if(applicationObject instanceof File){
			File file = (File)applicationObject;
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
