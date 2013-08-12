package vidada.model.metadata;

/**
 * Known Vidada MetaData Attributes
 * @author IsNull
 *
 */
public enum MediaMetaAttribute {

	/**
	 * Default vidada hash id
	 * 
	 */
	FileHash("vidada.hash"),

	/**
	 * The tags of this media
	 */
	Tags("vidada.tags"),
	/**
	 * The rating of this media
	 */
	Rating("vidada.rating"),
	;

	private String attributeName;

	private MediaMetaAttribute(String attributeName){
		this.setAttributeName(attributeName);
	}

	public String getAttributeName() {
		return attributeName;
	}

	private void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}
