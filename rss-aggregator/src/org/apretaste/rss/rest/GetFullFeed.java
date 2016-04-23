package org.apretaste.rss.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apretaste.rss.ContentTranslator;
import org.apretaste.rss.FeedAggregator;
import org.apretaste.rss.model.Feed;
import org.apretaste.rss.model.NewsService;
import org.restlet.resource.Get;

/**
 * Return one full feed
 * @author mark
 *
 */
public class GetFullFeed extends NewsService {

	@Get("json")
	public Feed doGet() {
		String feedLink = this.getAttribute("link");
		try {
			feedLink = URLDecoder.decode(feedLink, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Feed feed = FeedAggregator.getInstance().getFeedByLink(feedLink);
		
		if (this.hasLanguageOption()) {
			ContentTranslator translator = ContentTranslator.getInstance();
			
			//Translate Feed contents
			translator.translate(feed, this.getLanguageOption());
		}

		return feed;
	}
}
