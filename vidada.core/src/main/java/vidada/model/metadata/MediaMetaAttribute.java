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

    /**
     * Enum Constructor
     * @param attributeName
     */
	private MediaMetaAttribute(String attributeName){
        this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}

}
