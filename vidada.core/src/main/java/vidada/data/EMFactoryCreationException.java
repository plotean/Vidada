package vidada.data;

/**
 * THis exception is thrown when the Entity Manager (EM) Factory
 * could not be created.
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class EMFactoryCreationException extends RuntimeException {

	public  EMFactoryCreationException(String message) {
		this(message, null);
	}
	public  EMFactoryCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
