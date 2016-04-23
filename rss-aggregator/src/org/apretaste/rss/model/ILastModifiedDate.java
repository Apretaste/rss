package org.apretaste.rss.model;

import java.sql.Date;

/**
 * Simple interface for objects with a lastModifiedDate attribute
 * @author mark
 *
 */
public interface ILastModifiedDate {

	/**
	 * Return last Modified Date
	 * @return
	 */
	public Date getLastModifiedDate();
}
