package org.apretaste.rss.model;

/**
 * Definition of all properties that can be used in the "properties" files for the RSS aggregator Service
 * @author mark
 *
 */
public interface IPropertiesDefault {
	
	/**
	 * Interval, in seconds, between scans for new Feed content
	 */
	public static final String FEED_SCAN_INTEVAL = "feedScanInterval";
	
	/**
	 * Port where the RSS service will listen
	 */
	public static final String REST_PORT = "restServerPort";

	/**
	 * Interface where the RSS service will listen
	 */
	public static final String REST_ADDRESS = "restServerAddress";

	/**
	 * Engine to use for translation, three values are accepted: lingo24, google, microsoft
	 */
	public static final String TRANSLATOR_ENGINE = "translationEngine";
	
	/**
	 * Yandex Translation service API Key
	 */
	public static final String  TRANSLATOR_YANDEX_APIKEY = "translator.yandex.apikey";
	
	/**
	 * User Key for Lingo24 authentication
	 */
	public static final String TRANSLATOR_LINGO24_USERKEY = "translator.lingo24.userKey";
	
	/**
	 * Microsoft Translation API Cliend-ID
	 */
	public static final String TRANSLATOR_MICROSOFT_CLIENTID = "translator.microsoft.clientId";
	
	/**
	 * Microsoft Translation API Secret
	 */
	public static final String TRANSLATOR_MICROSOFT_SECRET = "translator.microsoft.secret";
}
