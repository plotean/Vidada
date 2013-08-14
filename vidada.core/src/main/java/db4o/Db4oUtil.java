package db4o;

import vidada.data.SessionManager;

import com.db4o.ObjectContainer;
import com.db4o.foundation.ArgumentNullException;


public class Db4oUtil {

	/**
	 * Gets the requested object by the db4o internal id
	 * @param db
	 * @param id
	 * @return
	 */
	public static <T> T getById(ObjectContainer db, long id){
		T library;

		if(db == null)
			throw new ArgumentNullException("db");
		if(id > 0){
			library = db.ext().getByID(id);
			db.activate(library, SessionManager.ACTIVATION_DEEPTH);
		}else{
			library = null;
		}
		return library;
	}

	/*
	@Override
	public MediaLibrary getById(long id) {
		ObjectContainer db =  SessionManager.getObjectContainer();
		System.out.println("MediaLibraryService:getById: " + id);
		MediaLibrary library = db.ext().getByID(id);
		db.activate(library, SessionManager.ACTIVATION_DEEPTH);
		return library;
	}
	 */
}
