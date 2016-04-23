package org.apretaste.rss.exception;

/**
 * Represents a Exception Generaing while Storing or Retrieving Feeds and Articles
 * @author mark
 *
 */
public class GeneralPersistenceException extends GeneralException {

	private static final long serialVersionUID = -4960964646456496260L;

	public GeneralPersistenceException() {
		// TODO Auto-generated constructor stub
	}

	public GeneralPersistenceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public GeneralPersistenceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public GeneralPersistenceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public GeneralPersistenceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
