package vidada.model.media;

/**
 * Specifies all sort possibilites
 * @author IsNull
 *
 */
public enum OrderProperty
{

	FILENAME("File Name", "filename"),
	OPENED("Play count", "opened"),
	ADDEDDATE("Add Date", "addedDate"),
	RATING("Rating", "rating");

	private final String description;
	private final String property;

	OrderProperty(String description, String propertyName){
		this.description = description;
		this.property = propertyName; 
	}

	public String getDescription() {
		return description;
	}

	public String getProperty() {
		return property;
	}


	@Override
	public String toString(){return getDescription();}
}