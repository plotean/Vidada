package vidada;

import java.io.File;
import java.util.List;

import vidada.data.SessionManager;
import vidada.model.libraries.LibraryEntry;
import vidada.model.libraries.MediaLibrary;
import vidada.model.user.User;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

public class TestDB {
	public static void main(String[] args) {


		File dbPath = new File("/Users/IsNull/Documents/test.db");

		ObjectContainer db = SessionManager.createObjectContainer(dbPath);
		{
			User testUser = new User("Blub");
			MediaLibrary library = new MediaLibrary();

			LibraryEntry entry = new LibraryEntry(library, testUser, null);

			db.store(entry);

			System.out.println(entry.toString());

			db.commit();
		}db.close();


		db = SessionManager.createObjectContainer(dbPath);
		{


			Query query = db.query();
			query.constrain(LibraryEntry.class);
			List<LibraryEntry> entries = query.execute();

			for (LibraryEntry entry : entries) {
				System.out.println(entry.toString());

			}

			db.commit();
		}db.close();


	}
}
