package org.apretaste.rss.exception;

/**
 * Indicates that the intended object was not found in the Data store
 * @author mark
 *
 */
public class ObjectNotFoundException extends GeneralPersistenceException {

	private static final long serialVersionUID = -5879621010607805097L;

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}

}
