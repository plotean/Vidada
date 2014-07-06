package vidada.model.user;

import vidada.model.entities.BaseEntity;

import java.security.Principal;

/**
 * Represents a User
 * @author IsNull
 *
 */
public class User extends BaseEntity implements Principal {

	private String name;

	public User() {	}


	public User(String username){
		setName(username);
	}

    @Override
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
