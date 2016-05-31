package org.apretaste.rss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apretaste.rss.model.Article;
import org.apretaste.rss.model.Category;
import org.apretaste.rss.model.Feed;
import org.apretaste.rss.model.IPropertiesDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton Class/Service for Monitoring and updating all feeds registered into
 * the system
 * 
 * @author mark
 *
 */
public class FeedAggregator implements IPropertiesDefault {

	private class FeedMonitor implements Runnable {
		/**
		 * Control variable for telling when to shutdown the system
		 */
		public volatile boolean enabled = false;

		@Override
		public void run() {
			// Decrease priority to ensure real-time requests are not impacted
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			logger.info("Feed Aggregator - Starting");
			this.enabled = true;
			while (this.enabled) {
				logger.debug("Feed Aggregator - Running Monitoring Loop");

				// Look for Updates in the Monitored Feeds
				updateFeeds();

				synchronized (this) {
					try {
						//Default interval is 30 seconds
						int interval = 30000;
						try {
							interval = PropertiesReader.getInstance().getIntProperty(FEED_SCAN_INTEVAL);
							
							//Convert to milliseconds
							interval = interval * 60 * 1000;
						} catch (Exception e){}

						this.wait(interval);
					} catch (InterruptedException eIgnore) {
					}
				}
			}
			logger.info("Feed Aggregator - Shutting Down");
		}

		/**
		 * Notifies the service to stop running
		 */
		public synchronized void stop() {
			logger.debug("Feed Aggregator Shutting Down Requested");

			this.enabled = false;

			this.notifyAll();
		}
	}

	/**
	 * Singleton instance of this service/class
	 */
	private static FeedAggregator singleton;

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(FeedAggregator.class);

	/**
	 * Daemon for monitoring Feeds
	 */
	private FeedMonitor monitor;

	/**
	 * Feeds to monitor, optimized for concurrency
	 */
	private volatile SortedSet<Feed> feeds;

	/**
	 * Set of Categories for quick access
	 */
	private volatile SortedMap<String, Category> categories = new TreeMap<>();
	
	/**
	 * Set of Categories for quick access, Key is ID
	 */
	private volatile SortedMap<String, Category> categoriesById = new TreeMap<>();
	
	/**
	 * Feeds indexed by link
	 */
	private volatile Map<String, Feed> feedByLink = new HashMap<>();
	
	/**
	 * Articles indexed by Link and GUID
	 */
	private volatile Map<String, Article> articleByLinkOrID = new HashMap<>();

	/**
	 * Constructor
	 */
	private FeedAggregator() {
		//Load Categories
		for (Category cat : PropertiesReader.getInstance().getCategories()) {
			this.categories.put(cat.getTitle().toUpperCase(), cat);
			
			this.categoriesById.put(cat.getId(), cat);
		}

		this.monitor = new FeedMonitor();

		// Initialize Monitoring Thread
		Thread thrMonitor = new Thread(this.monitor);
		thrMonitor.setName("FeedAggregator Serviced");
		thrMonitor.setDaemon(true);

		thrMonitor.start();
	}

	/**
	 * Returns the singleton instance for the Feed Aggregation Service
	 * 
	 * @return Singleton Instance of Feed Aggregator
	 */
	public static synchronized FeedAggregator getInstance() {
		if (singleton == null) {
			singleton = new FeedAggregator();
		}

		return singleton;
	}

	/**
	 * Return a read only Set of the Category the Feeds currently being monitored
	 * @return
	 */
	public SortedSet<Category> getCategories() {
		SortedSet<Category> catList = new TreeSet<>();
		for(Category category : this.categories.values()) {
			catList.add(category.clone());
		}
		return catList;
	}
	
	/**
	 * Retrieve a Category by its ID
	 * @param _id
	 * @return
	 */
	public Category getCategoryById(String _id) {
		Category cat = this.categoriesById.get(_id);
		
		if (cat == null) {
			return null;
		} else {
			return cat.clone();
		}
	}
	
	/**
	 * Retrieve a Category by its Title
	 * @param _title
	 * @return
	 */
	public Category getCategoryByTitle(String _title) {
		Category cat = this.categories.get(_title.toUpperCase());
		
		if (cat == null) {
			return null;
		} else {
			return cat.clone();
		}
	}

	/**
	 * Reload all Feed information from the configuration file
	 */
	private synchronized void updateFeeds() {
		logger.info("Scanning RSS Feeds - Start");

		// Reload Feed list from Database, ensure that all Feeds are loaded
		try {
			SortedSet<Feed> updateFeeds = new TreeSet<>();

			for (Category category : this.categories.values()) {
				logger.info("Refreshing Category '" + category.getTitle() + "', with " + category.getFeedCount() + " feeds.");
				Collection<Feed> catFeeds = new ArrayList<>(); 
				catFeeds.addAll(category.getFeeds()); 
				for (Feed feed : catFeeds) {
					logger.info("Checking RSS Feed - Feed Title: " + feed.getTitle() + ", Feed Link: " + feed.getLink());

					try {
						Feed freshFeed = FeedDownloader.getInstance().fetchOnlineFeed(feed.getLink());

						if (feed.getPubDate()==null || freshFeed.getPubDate().after(feed.getPubDate())) {
							logger.info("Newer data for Feed '" + feed.getTitle() + "' found, refreshing");

							freshFeed.setSourceName(feed.getSourceName());
							updateFeeds.add(freshFeed);
							category.removeFeed(feed);
							category.addFeed(freshFeed);							
						}
					} catch (Exception e) {
						logger.error("Error Updating Feed: " + feed.getTitle() + ", Feed Link: " + feed.getLink(), e);
					}
				}
			}

			//Everything is good, update category and feed collections
			this.feeds = Collections.unmodifiableSortedSet(updateFeeds);
			
			//Index all Feeds by its link
			Map<String, Feed> tempFeedByLink = new HashMap<>();
			Map<String, Article> tempArticleByLink = new HashMap<>();
			for (Feed feed: this.feeds) {
				tempFeedByLink.put(feed.getLink(), feed);
				
				//Index Articles
				for (Article art : feed.getArticles()) {
					//Index by Link
					if (art.getLink()!=null & !art.getLink().isEmpty()) {
						tempArticleByLink.put(art.getLink(), art);
					}
					
					//Index by GUID
					if (art.getGuid()!=null & !art.getGuid().isEmpty()) {
					tempArticleByLink.put(art.getGuid(), art);
					}
				}
			}
			this.feedByLink = tempFeedByLink;
			this.articleByLinkOrID = tempArticleByLink;
		} catch (Exception e) {
			logger.error("Could not read full list of Feeds from Database", e);
		}

		logger.info("Scanning RSS Feeds - End");
	}

	/**
	 * Return a read only list of all feeds currently monitored
	 * @return
	 */
	public SortedSet<Feed> getFeeds() {
		SortedSet<Feed> feeds = new TreeSet<>();
		for (Feed feed : this.feeds) {
			feeds.add(feed.clone());
		}
		return feeds;
	}

	/**
	 * Return the Feeds for a given Category
	 * @param _categoryTitle
	 * @return
	 */
	public SortedSet<Feed> getFeeds(String _categoryTitle) {
		if (this.categories.containsKey(_categoryTitle.toUpperCase())) {
			SortedSet<Feed> feeds = new TreeSet<>();
			for (Feed feed : this.categories.get(_categoryTitle.toUpperCase()).getFeeds()) {
				feeds.add(feed.clone());
			}
			return feeds;
		} else {
			//Return empty Set
			return new TreeSet<>();
		}
	}
	
	/**
	 * Find a feed by its Link
	 * The Link works a s a unique ID for a Feed
	 * @param _feedLink
	 * @return
	 */
	public Feed getFeedByLink(String _feedLink) {
		Feed feed = this.feedByLink.get(_feedLink);
		
		if (feed == null) {
			return null;
		} else {
			return feed.clone();
		}
	}

	/**
	 * Find an Article by its Link or its ID
	 * The Link works a s a unique ID for an Article
	 * @param _feedLink
	 * @return
	 */
	public Article getArticleByLinkOrID(String _articleLink) {
		return this.articleByLinkOrID.get(_articleLink);
	}
	
	/**
	 * Notifies the service to stop running
	 */
	public synchronized void shutdown() {
		this.monitor.stop();
	}
}
