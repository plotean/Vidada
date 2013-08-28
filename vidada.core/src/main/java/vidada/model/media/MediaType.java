package vidada.model.media;

/**
 * Lists all available media types
 * 
 * Note: Fake Enum since db4o seems to have trouble with real Enums on android.
 * @author IsNull
 *
 */
public final class MediaType
{
	public static final MediaType NONE = new MediaType(0x00, "None");
	public static final MediaType ANY = new MediaType(0xFF,"All");
	public static final MediaType MOVIE = new MediaType(0x01,"Movies");
	public static final MediaType IMAGE = new MediaType(0x02,"Images");

	private byte id;
	private String displayName;

	MediaType(){ }

	MediaType(int id, String displayName){
		this.id = (byte)id;
		this.displayName = displayName;
	}




	@Override
	public String toString(){
		return displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaType other = (MediaType) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static MediaType[] values() {
		return new MediaType[]{NONE, ANY, MOVIE, IMAGE};
	}


}


/**
 * Lists all available media types
 * @author IsNull
 *

public enum MediaType implements EnumConverter<MediaType> {

	NONE(0x00, "None"), ANY(0xFF,"All"), MOVIE(0x01,"Movies"), IMAGE(0x02,"Images");

	private final byte id;
	private final String displayName;
	transient private final static ReverseEnumMap<MediaType> map =
			new ReverseEnumMap<MediaType>(MediaType.class);

	MediaType(int id, String displayName){
		this.id = (byte)id;
		this.displayName = displayName;
	}

	@Override
	public String toString(){
		return displayName;
	}


	@Override
	public byte convert() {
		return id;
	}

	@Override
	public MediaType convert(byte val) {
		return map.get(val);
	}
}
 */
