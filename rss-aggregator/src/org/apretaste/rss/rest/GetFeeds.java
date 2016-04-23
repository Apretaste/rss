package org.apretaste.rss.rest;

import java.util.Set;

import org.apretaste.rss.ContentTranslator;
import org.apretaste.rss.FeedAggregator;
import org.apretaste.rss.model.Feed;
import org.apretaste.rss.model.IPropertiesDefault;
import org.apretaste.rss.model.NewsService;
import org.restlet.resource.Get;

/**
 * Return all feeds from a given category
 * @author mark
 *
 */
public class GetFeeds extends NewsService implements IPropertiesDefault {

	@Get("json")
	public Set<Feed> doGet() {
		String catTitle = this.getAttribute("category");

		Set<Feed> feeds = FeedAggregator.getInstance().getFeeds(catTitle);
		
		if (this.hasLanguageOption()) {
			ContentTranslator translator = ContentTranslator.getInstance();
			
			//Translate Feed contents
			for (Feed feed : feeds) {
				translator.translate(feed, this.getLanguageOption());
			}
		}

		return feeds;
	}
}
