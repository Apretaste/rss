package org.apretaste.rss.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A RSS feed
 * @author mark
 *
 */
public class Feed implements Comparable<Feed>, Cloneable {

	/**
	 * RSS feed url from which to fetch it
	 */
	private String link;

	/**
	 * @ letter ISO code for the language of this Feed
	 */
	private String language;

	/**
	 * RSS Feed's title
	 */
	private String title;

	/**
	 * RSS Feed's description
	 */
	private String description;

	/**
	 * Publication Date
	 */
	private Date pubDate;

	/**
	 * Category to which this feed pertains
	 */
	@JsonIgnore
	private Category category;

	/**
	 * Name of the news outlet or website from which this Feed is being consumed
	 */
	private String sourceName;

	/**
	 * Copyright notice
	 */
	private String copyright;

	/**
	 * Articles under this category
	 */
	private SortedSet<Article> articles = new TreeSet<>();

	/**
	 * Includes an Article to this Feed
	 * @param _article
	 */
	public void addArticle(Article _article) {
		_article.setFeed(this);
		this.articles.add(_article);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public SortedSet<Article> getArticles() {
		return articles;
	}

	protected void setArticles(SortedSet<Article> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return "Feed [title=" + title + ", language=" + this.getLanguage() + ", description=" + description
				+ ", link=" + link + ", pubDate: " + this.getPubDate() + ", articleCount=" + this.getArticleCount() + "]";
	}

	/**
	 * Current article count in this feed
	 * @return
	 */
	public int getArticleCount() {
		return this.articles.size();
	}

	/**
	 * Compare by Publication Date and Link (unique)
	 * @param _feed {@link Feed2} instance to compare
	 * @return pubDate comparison
	 */
	@Override
	public int compareTo(Feed _feed) {
		String thisObj = ((this.getTitle()==null) ? "" : this.getTitle()) + this.getLink();
		String otherObj = ((_feed.getTitle()==null) ? "" : _feed.getTitle()) + _feed.getLink();

		return thisObj.compareTo(otherObj);
	}

	@Override
	public int hashCode() {
		return this.getLink().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public Feed clone() {
		try {
			Feed feed = (Feed)super.clone();

			feed.articles = new TreeSet<>();

			for (Article art : this.articles) {
				feed.addArticle(art.clone());
			}

			return feed;

		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
