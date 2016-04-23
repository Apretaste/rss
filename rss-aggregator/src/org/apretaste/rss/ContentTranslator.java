package org.apretaste.rss;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.translate.GoogleTranslator;
import org.apache.tika.language.translate.Lingo24TranslatorHack;
import org.apache.tika.language.translate.MicrosoftTranslator;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.language.translate.YandexTranslator;
import org.apretaste.rss.model.Article;
import org.apretaste.rss.model.Feed;
import org.apretaste.rss.model.IPropertiesDefault;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provided realtime translation of Article content (Title, Description).
 * Requests are cachede in order to save in Requests to the translation service
 * @author mark
 *
 */
public class ContentTranslator implements IPropertiesDefault {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ContentTranslator.class);

	/**
	 * Singleton instance of service
	 */
	private static ContentTranslator singleton;
	
	/**
	 * Expiration in seconds for cached translations, 7 days by default
	 */
	private static int CACHE_EXPIRATION = 60 * 60 * 24 * 7;

	/**
	 * Cache of translations already done
	 */
	private ConcurrentMap<String, String> translationCache;

	/**
	 * MapDB engine
	 */
	private DB mapDB;

	/**
	 * MS Translation API Client
	 */
	private Translator translator;

	public static synchronized ContentTranslator getInstance() {
		if (singleton==null) {
			singleton = new ContentTranslator();
		}
		return singleton;
	}

	/**
	 * Contructor
	 */
	private ContentTranslator() {
		//Prepare translation API client
		//this.translator = new CachedTranslator(msTranslator);
		PropertiesReader properties = PropertiesReader.getInstance();

		String translatorEngine = properties.getProperty(TRANSLATOR_ENGINE);
		if (translatorEngine!=null && !translatorEngine.isEmpty()) {
			switch (translatorEngine) {
			case "yandex":
				YandexTranslator yandexTranslator = new YandexTranslator();
				
				//Lingo24 Credentials
				yandexTranslator.setApiKey(properties.getProperty(TRANSLATOR_YANDEX_APIKEY));
				yandexTranslator.setFormat("html");
				
				this.translator = yandexTranslator;
				break;
			case "lingo24":
				Lingo24TranslatorHack extTranslator = new Lingo24TranslatorHack();
				
				//Lingo24 Credentials
				extTranslator.setUserKey(properties.getProperty(TRANSLATOR_LINGO24_USERKEY));
				
				this.translator = extTranslator;
				break;
			case "google":
				GoogleTranslator googleTranslator = new GoogleTranslator();
				
				this.translator = googleTranslator;
				break;
			case "microsoft":
				MicrosoftTranslator msTranslator = new MicrosoftTranslator();
				
				//Microsoft Credentials
				msTranslator.setId(properties.getProperty(TRANSLATOR_MICROSOFT_CLIENTID));
				msTranslator.setSecret(properties.getProperty(TRANSLATOR_MICROSOFT_SECRET));
				
				this.translator = msTranslator;
				break;
			default:
				logger.error("unknown Translation Engine '" + translatorEngine + "' defined, this feature will be disabled");
			}
		} else {
			logger.info("No engine defined for Real-Time Translation, this feature will be disabled");
		}

		if (this.translator!=null) {
			if (this.translator.isAvailable()) {
				logger.info("Translation Client ready for use " + this.translator.getClass().getName());
				
				//Initialize Translation Cache
				try {
					File temp = File.createTempFile("xxx", "xxx");
					File dbFile = new File(temp.getParent(),"translationCache.mapDB");
					temp.delete();
					
					this.mapDB = DBMaker.fileDB(dbFile.getAbsolutePath()).make();
					this.translationCache = this.mapDB.hashMap("translation", Serializer.STRING, Serializer.STRING)
							.expireAfterCreate(CACHE_EXPIRATION*1000)
							.expireAfterGet(CACHE_EXPIRATION*1000)
							.counterEnable()
							.createOrOpen();
					
					if (logger.isInfoEnabled()) {
						logger.info("Translation Cache Initialized with '" + this.translationCache.size() + "' results already stored.");
					}
				} catch (IOException e) {
					logger.warn("Could not initialize MapDB sub-system, translation caching will be done in-memory", e);
					
					//Use in-memory Caching
					this.mapDB = DBMaker.memoryDB().make();
					this.translationCache = this.mapDB.hashMap("translation", Serializer.STRING, Serializer.STRING)
							.expireAfterCreate(CACHE_EXPIRATION*1000)
							.expireAfterGet(CACHE_EXPIRATION*1000)
							.counterEnable()
							.create();
				}
			} else {
				logger.error("Translation Client configured but NOT ready for use " + this.translator.getClass().getName());
			}
		} else {
			logger.warn("Translation Client Disabled");
		}
	}

	/**
	 * Translates text from source language to target language
	 * @param _text Text to translate
	 * @param _sourceLanguage Language of the input text
	 * @param _targetLanguage Language into which translate
	 * @return Translated text, same value as input if translation was not possible
	 */
	public String translate(String _text, String _sourceLanguage, String _targetLanguage) {
		//If source and target language are the same, do not perform translation
		if (this.translator==null || _text==null || _text.trim().isEmpty() || _sourceLanguage.equalsIgnoreCase(_targetLanguage)) {
			return _text;
		}
		
		//Key for translated text
		String key = _text.hashCode() + "_" + _sourceLanguage.toLowerCase() + '-' + _targetLanguage.toLowerCase();

		String result = this.translationCache.get(key);

		if (result !=null) {
			//Already Translated
			
			if (logger.isDebugEnabled()) {
				logger.debug("Text Already Translated, Key: " + key + ", Length: " + _text.length());
			}
			
			return result;
		} else {
			//First time being translated
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Text Requires Translation, Key: " + key + ", Length: " + _text.length() + ", Text: " + _text.substring(0, Math.min(20, _text.length())));
				}

				//Request translation
				result = translator.translate(_text, _sourceLanguage, _targetLanguage);
				//result = "TEST: " + _text;
				
				//Store translation
				if (result!=null && !result.isEmpty()) {
					this.translationCache.putIfAbsent(key, result);
					
					//Save Cached entry
					try {
						this.mapDB.commit();
					} catch (Exception e) {
						logger.warn("Could not Commit Translation Cache to Disk", e);
					}
					
					if (logger.isDebugEnabled()) {
						logger.debug("Text Successfully Translated, Key: " + key + ", From(" + _text.length() + "): " + _text.substring(0, Math.min(20, _text.length())) + "... ,To(" + result.length() + "): " + result.substring(0, Math.min(20, result.length())) + "...");
					}
				}
				
				return result;
			} catch (TikaException | IOException e) {
			//} catch (Exception e) {
				logger.error("Could not translate text from '" + _sourceLanguage + "' into '" + _targetLanguage + "'", e);
				return _text;
			}
		}
	}
	
	/**
	 * Performs in-place translation of an Article
	 * @param _article {@link Article} to transalte
	 * @param _targetLanguage Target Language of the translation
	 */
	public void translate(Article _article, String _targetLanguage) {
		try {
			_article.setTitle(this.translate(_article.getTitle(), _article.getFeed().getLanguage(), _targetLanguage));
			_article.setDescription(this.translate(_article.getDescription(), _article.getFeed().getLanguage(), _targetLanguage));
		} catch (Exception e) {
			logger.error("Could not translate Article '" + _article.getTitle() + "' from '" + _article.getFeed().getLanguage() + "' to '" + _targetLanguage + "'", e);
		}
	}
	
	/**
	 * Performs in-place translation of a Feed and all of its Articles
	 * @param _feed {@link Feed} to translate
	 * @param _targetLanguage Languange into which translate the content
	 */
	public void translate(Feed _feed, String _targetLanguage) {
		if (_targetLanguage!=null && _feed!=null && !_feed.getLanguage().equalsIgnoreCase(_targetLanguage)) {
			//Translate Feed Information
			_feed.setTitle(this.translate(_feed.getTitle(), _feed.getLanguage(), _targetLanguage));
			_feed.setDescription(this.translate(_feed.getDescription(), _feed.getLanguage(), _targetLanguage));
			
			//Translate Articles
			for (Article article : _feed.getArticles()) {
				try {
				this.translate(article, _targetLanguage);
				} catch (Exception e) {
					logger.error("Could not translate Feed '" + _feed.getTitle() + "' from '" + _feed.getLanguage() + "' to '" + _targetLanguage + "'", e);
				}
			}
		}
	}


	@Override
	protected void finalize() throws Throwable {
		//Close MapDB
		this.mapDB.close();

		this.mapDB = null;
	}

}
