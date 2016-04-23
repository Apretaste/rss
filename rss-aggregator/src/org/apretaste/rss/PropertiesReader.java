package org.apretaste.rss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apretaste.rss.model.Category;
import org.apretaste.rss.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Easy access to the properties file
 * @author mark
 *
 */
public class PropertiesReader {

	/**
	 * Singleton Instance
	 */
	private static PropertiesReader singleton;

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

	/**
	 * Service Properties File
	 */
	private Properties properties;

	/**
	 * All configured Categories
	 */
	private SortedSet<Category> categories = new TreeSet<>();

	public static synchronized PropertiesReader getInstance() {
		if (singleton == null) {
			singleton = new PropertiesReader();
		}

		return singleton;
	}

	private PropertiesReader() {
		//Read properties file from classpath

		try {
			logger.info("Reading Properties File");

			Properties tempProperties = new Properties();
			tempProperties.load(PropertiesReader.class.getResourceAsStream("/server.properties"));

			this.properties = tempProperties;

			//Read Categories and Feeds
			this.categories.addAll(this.readCategories());
			this.categories = Collections.unmodifiableSortedSet(this.categories); 
		} catch (NullPointerException e) {
			logger.error("Properties file not found", e);
		} catch (IOException e) {
			logger.error("Could not read properties file", e);
		}
	}

	/**
	 * Service wide properties
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Return the value of the property with the given key
	 * @param _key Property Key
	 * @return Property Value
	 */
	public String getProperty(String _key) {
		String value = properties.getProperty(_key);
		
		if (value == null) {
			return null;
		} else {
			return value.trim();
		}
	}

	/**
	 * Return the value of the property with the given key, the value is returned as an
	 * Integer or <code>null</code> if no value was set or the value cannot be parsed as an integer
	 * @param _key
	 * @return
	 */
	public Integer getIntProperty(String _key) {
		String value = properties.getProperty(_key);

		if (value == null) {
			//Property not set
			return null;
		} else {
			try {
				return Integer.parseInt(value, 10);
			} catch (Exception e) {
				logger.warn("Interget Property not property set, Key: '" + _key + "', Value: '" + value + "'");
				return null;
			}
		}
	}

	/**
	 * All configured categories
	 * @return
	 */
	public SortedSet<Category> getCategories() {
		return categories;
	}

	/**
	 * Read all Categories defined in the properties file, along with its Feeds 
	 * @return
	 */
	private List<Category> readCategories() {
		List<Category> categories = new ArrayList<>();

		String catPattern = "category.";
		String feedPattern = "feed.";

		//Read Categories
		for (int i=1; i<1024; i++) {
			String title = this.properties.getProperty(catPattern + i + ".title");
			String desc = this.properties.getProperty(catPattern + i + ".description");

			if (title==null) {
				break;
			}

			//Add Category
			Category cat = new Category();
			cat.setTitle(title);
			cat.setDescription(desc);
			categories.add(cat);
		}

		//Read Feeds
		for (int i=1; i<1024; i++) {
			String link = this.properties.getProperty(feedPattern + i + ".link");
			String sourceName = this.properties.getProperty(feedPattern + i + ".sourceName");

			if (link==null) {
				break;
			}

			//Add Feed
			int categoryId = Integer.parseInt(this.properties.getProperty(feedPattern + i + ".category"),10);

			Feed feed = new Feed();
			feed.setLink(link);
			feed.setSourceName(sourceName);
			categories.get(categoryId-1).addFeed(feed);
		}

		//Return results
		return categories;
	}

}
