package org.apretaste.rss.exception;

/**
 * Represents a Exception Generaing while Processing, Sotring or Retrieving Feeds and Articles
 * @author mark
 *
 */
public class GeneralException extends Exception {

	private static final long serialVersionUID = -7495969915282039231L;

	public GeneralException() {
		super();
	}

	public GeneralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralException(String message) {
		super(message);
	}

	public GeneralException(Throwable cause) {
		super(cause);
	}

}
