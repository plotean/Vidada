package vidada.model.entities;


import vidada.data.SessionManager;
import archimedesJ.IPrototypable;
import archimedesJ.data.ObservableBean;

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
		ObjectContainer db =  SessionManager.getObjectContainer();
		return db.ext().getID(db);
	}
}
