package org.apretaste.rss.model;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * A Feed category
 * @author mark
 */
public class Category implements Comparable<Category>, Cloneable {

	/**
	 * Random generated GUID
	 */
	private String id;

	/**
	 * Feeds for this Category
	 */
	private SortedSet<Feed> feeds = new TreeSet<>();

	/**
	 * Title of the category
	 */
	private String title;

	/**
	 * Description for this Category
	 */
	private String description;

	/**
	 * Add a feed to the Category
	 * @param _feed
	 */
	public void addFeed(Feed _feed) {
		_feed.setCategory(this);
		this.feeds.add(_feed);
	}

	/**
	 * Remove a feed from the Category
	 * @param _feed
	 */
	public void removeFeed(Feed _feed) {
		_feed.setCategory(null);
		this.feeds.remove(_feed);
	}


	public SortedSet<Feed> getFeeds() {
		return feeds;
	}

	protected void setFeeds(SortedSet<Feed> feeds) {
		this.feeds = feeds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		this.setId(DigestUtils.md5Hex(title.getBytes()));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the current Feed Count
	 * @return
	 */
	public int getFeedCount() {
		return this.feeds.size();
	}

	/**
	 * Get the full number of articles in the category
	 * @return
	 */
	public int getArticleCount() {
		int total = 0;

		for (Feed feed : this.getFeeds()) {
			total += feed.getArticleCount();
		}

		return total;
	}

	/**
	 * Compare by Title only
	 */
	@Override
	public int compareTo(Category _category) {
		return this.getTitle().compareTo(_category.getTitle());
	}

	@Override
	public Category clone() {
		try {
			Category cat = (Category)super.clone();

			cat.feeds = new TreeSet<>();

			for (Feed feed : this.getFeeds()) {
				cat.addFeed(feed.clone());
			}

			return cat;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
