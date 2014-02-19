package vidada.data;

/**
 * THis exception is thrown when the Entity Manager (EM) Factory
 * could not be created.
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class DatabaseConnectionException extends RuntimeException {

	public  DatabaseConnectionException(String message) {
		this(message, null);
	}
	public  DatabaseConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
