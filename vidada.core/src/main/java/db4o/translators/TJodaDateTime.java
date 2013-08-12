package db4o.translators;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

public class TJodaDateTime implements ObjectConstructor{

	@Override
	public Object onInstantiate(ObjectContainer container, Object storedObject) {
		Long milliseconds = (Long)storedObject;
		return new org.joda.time.DateTime(milliseconds.longValue());
	}

	@Override
	public Object onStore(ObjectContainer container, Object applicationObject) {
		org.joda.time.DateTime dt = (org.joda.time.DateTime)applicationObject;
		return new Long(dt.getMillis());
	}

	@Override
	public void onActivate(ObjectContainer container,
			Object applicationObject,
			Object storedObject) {
		// do nothing
	}

	@Override
	public Class storedClass() {
		return long.class;
	}

}