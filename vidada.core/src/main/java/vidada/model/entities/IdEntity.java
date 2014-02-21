package vidada.model.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Represents an entity with an auto-generated integer id.
 * 
 * @author IsNull
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
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
