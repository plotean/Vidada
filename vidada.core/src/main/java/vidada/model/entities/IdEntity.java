package vidada.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents an entity with an auto-generated integer id.
 * 
 * @author IsNull
 *
 */
@Entity
public abstract class IdEntity extends BaseEntity {

	@Id
	@GeneratedValue
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id){
		this.id = id;
	}
}
