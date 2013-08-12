package vidada.model.media;

/**
 * Lists all available media types
 * @author IsNull
 *
 */
public enum MediaType {
	ANY("All"), MOVIE("Movies"), IMAGE("Images");
	
	private final String displayName;

	MediaType(String displayName){
		this.displayName = displayName;
	}
	
	@Override
	public String toString(){
		return displayName;
	}
}
