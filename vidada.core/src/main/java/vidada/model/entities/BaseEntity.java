package vidada.model.entities;


import vidada.data.db4o.SessionManagerDB4O;
import archimedesJ.IPrototypable;
import archimedesJ.data.observable.ObservableBean;

import com.db4o.ObjectContainer;

/**
 * Base class for all DB Entities. 
 * Provides basic functionality, and  proper ID handling in equals.
 * @author IsNull
 */
public abstract class BaseEntity extends ObservableBean implements IPrototypable<BaseEntity> {

	@Override
	public void prototype(BaseEntity prototype) {

	}

	public long getId(){
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		return db.ext().getID(this); // db.ext().getObjectInfo(this).getUUID();
	}
}
