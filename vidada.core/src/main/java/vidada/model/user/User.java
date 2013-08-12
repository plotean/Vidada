package vidada.model.user;

import java.util.List;

import vidada.data.SessionManager;
import vidada.model.entities.BaseEntity;
import archimedesJ.util.EnvironmentIdentifier;
import archimedesJ.util.OSValidator;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class User extends BaseEntity {

	private transient static User currentUser = null;
	private String name;

	public static User current(){

		if(currentUser == null)
		{
			//
			// we need a user + machine specific id
			//
			final String username = System.getProperty("user.name") + "@" + getMachineId();

			ObjectContainer db = SessionManager.getObjectContainer();

			List<User> users = db.query(new Predicate<User>()  {
				@Override
				public boolean match(User user)  {
					return user.getName().equals(username);
				}
			});

			if(users.isEmpty())
			{
				currentUser = new User(username);
				db.store(currentUser);
				System.out.println("created new user: " + currentUser);
			}else{
				currentUser = users.get(0);
				System.out.println("found existing user: " + currentUser);
			}

		}

		return currentUser;
	}

	private static String getMachineId(){
		if(OSValidator.isAndroid()){
			//
			// Since the machine id is used only to support 
			// sharing of the same database from different computers,
			// the ID is not really used by a mobile version.
			// (Mobile versions do only allow one internal DB.)
			//
			return "android";
		}
		return EnvironmentIdentifier.getInstance().environmentIdentifier();
	}




	public User() {	}


	public User(String username){
		setName(username);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString(){
		return getName();
	}

}
