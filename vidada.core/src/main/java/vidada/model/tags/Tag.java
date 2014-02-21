package vidada.model.tags;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;



/**
 * Represents a simple tag
 * @author IsNull
 *
 */
@Entity
public class Tag  implements Comparable<Tag> {

	transient int hashcode_cache = -1;

	@Id
	@Column(nullable=false)
	private String name;

	protected Tag() { }

	public Tag(String name){
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString(){
		return getName();
	}

	@Override
	public int hashCode() {
		if(hashcode_cache == -1){
			final int prime = 31;
			hashcode_cache = prime * 1 + ((name == null) ? 0 : name.hashCode());
		}
		return hashcode_cache;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tag o) {
		if(o == null) return -1;
		return this.getName().compareTo(o.getName());
	}


}
