package vidada.model.user;

import vidada.model.entities.BaseEntity;

/**
 * Represents a User
 * @author IsNull
 *
 */
public class User extends BaseEntity {

	private String name;

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
