package vidada.model.tags;

/**
 * Represents the logical state of a Tag
 * 
 * @author IsNull
 *
 */
public enum TagState {

	/**
	 * Unknown / not defined State
	 */
	None,


	/**
	 * Represents a tag which is allowed but not required.
	 */
	Allowed,

	/**
	 * Represents an required / selected tag
	 */
	Required,

	/**
	 * Represents an active negated tag which should be blocked
	 */
	Blocked,

	/**
	 * Represents an unclear / conflicting logocal state
	 * (Such as when a State has to represent two different child states)
	 */
	Indeterminate,

	/**
	 * Currently unavailable
	 */
	Unavaiable

}
