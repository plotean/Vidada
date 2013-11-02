package vidada.model.tags;

/**
 * Represents the logical filter state of a Tag
 * 
 * @author IsNull
 *
 */
public enum TagFilterState {

	/**
	 * Represents a tag which is allowed but not required.
	 */
	Allowed,

	/**
	 * Represents an required tag
	 */
	Required,

	/**
	 * Represents an active negated tag which should be blocked
	 */
	Blocked,

}
