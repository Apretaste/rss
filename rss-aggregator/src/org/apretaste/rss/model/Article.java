package org.apretaste.rss.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A single Article from a RSS feed
 * Articles only exist in memory, they are never stored in the database
 * @author mark
 *
 */
public class Article implements Comparable<Article>, Cloneable {

	/**
	 * Feed to which this Article pertains
	 */
	@JsonIgnore
	private Feed feed;

	/**
	 * Source to fetch the full article
	 */
	private String link;

	/**
	 * Unique identifier, in the feed, for this article
	 */
	private String guid;

	/**
	 * Publication Date
	 */
	private Date pubDate;

	/**
	 * Author name, if available
	 */
	private String author;

	/**
	 * Title of this Article
	 */
	private String title;
	
	/**
	 * Short description of the article
	 */
	private String description;

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "FeedMessage [title=" + title + ", description=" + description
				+ ", link=" + link + ", author=" + author + ", guid=" + guid
				+ ", pubDate: " + this.getPubDate() + "]";
	}

	/**
	 * Order by publication Date and GUID (unique)
	 * @param _article {@link Article} to compare
	 * @return pubDate comparison
	 */
	@Override
	public int compareTo(Article _article) {
		String thisObj = ((this.getPubDate()==null) ? "" : this.getPubDate().getTime()) + this.getGuid();
		String otherObj = ((_article.getPubDate()==null) ? "" : _article.getPubDate().getTime()) + _article.getGuid();

		return otherObj.compareTo(thisObj);
	}

	@Override
	public Article clone() throws CloneNotSupportedException {
		Article art = (Article)super.clone();
		
		return art;
	}

	
}
