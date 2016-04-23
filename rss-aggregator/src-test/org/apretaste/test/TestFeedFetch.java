package org.apretaste.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.apretaste.rss.FeedDownloader;
import org.apretaste.rss.model.Article;
import org.apretaste.rss.model.Feed;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Test the fetching of online information on a feed
 * @author mark
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFeedFetch {

	/**
	 * Links to Feeds for testing
	 */
	private Collection<String> links = new ArrayList<>();
	
	@Before
	public void setUp() throws Exception {
		this.links.add("http://www.theguardian.com/music/musicblog/rss");
		this.links.add("http://www.martinoticias.com/api/epiqq");
		this.links.add("http://www.espnfc.com/rss");
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void test1Fetch() {
		for (String link : this.links) {
			Feed feed = FeedDownloader.getInstance().fetchOnlineFeed(link);
			
			assertNotNull("No Feed received for " + link, feed);
			
			assertTrue("Feed is empty: " + link, feed.getArticleCount()>0);
			
			assertNotNull(feed.getPubDate());
			
			//Check all basic fields for all Articles
			for (Article art : feed.getArticles()) {
				assertNotNull(art.getTitle());
				assertNotNull(art.getDescription());
				assertNotNull(art.getPubDate());
				assertNotNull(art.getGuid());
				assertNotNull(art.getLink());
			}
		}
	}

}
