package vidada.model.tags;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;



/**
 * Represents a simple tag
 * @author IsNull
 *
 */
@Entity
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.FIELD)
public class Tag  implements Comparable<Tag> {


	/***************************************************************************
	 *                                                                         *
	 * Public creator API                                                      *
	 *                                                                         *
	 **************************************************************************/

	public static Tag create(String tag){
		return new Tag(toTagString(tag));
	}

	public static String toTagString(String tag){
		// TODO Maybe replace special chars here as well
		return tag.trim().toLowerCase();
	}

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	transient int hashcode_cache = -1;

	@Id
	@Column(nullable=false)
	private String name;


	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	protected Tag() { }

	protected Tag(String name){
		this.setName(name);
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}


	/***************************************************************************
	 *                                                                         *
	 * Overridden Public API                                                   *
	 *                                                                         *
	 **************************************************************************/

	@Override
	public String toString(){
		return name;
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
