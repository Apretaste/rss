package org.apretaste.rss.model;

import org.restlet.resource.ServerResource;

/**
 * Simple definition for all RSS aggregator services
 * @author mark
 *
 */
public class NewsService extends ServerResource {

	/**
	 * Language for returning results, default is to use orignal languages
	 * Must be provided as a 2 digit code
	 */
	public static final String PARAM_LANGUAGE = "lang";
	
	/**
	 * Return the Language Option received with the request, <code>null</code> is none received
	 * @return
	 */
	protected final String getLanguageOption() {
		String langValue = this.getQueryValue(PARAM_LANGUAGE);
		
		if (langValue==null || langValue.trim().isEmpty()) {
			return null;
		} else {
			return langValue.trim().substring(0, 2).toLowerCase();
		}
	}
	
	/**
	 * Indicates whether a Language Option was received as parameter
	 * @return <code>true</code> if a language option was received
	 */
	protected boolean hasLanguageOption() {
		return this.getLanguageOption()!=null;
	}
}
