package vidada;

import java.io.File;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.user.User;

import com.db4o.ObjectContainer;

public class TestDB {
	public static void main(String[] args) {


		File dbPath = new File("/Users/IsNull/Documents/test.db");

		ObjectContainer db = SessionManagerDB4O.createObjectContainer(dbPath);
		{
			User testUser = new User("Blub");
			MediaLibrary library = new MediaLibrary();

			db.commit();
		}db.close();



	}
}
