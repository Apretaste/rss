package org.apretaste.test;

import static org.junit.Assert.*;

import org.apretaste.rss.FeedAggregator;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Test the RSS Monitor by adding and removing Feeds
 * Fetch behavior is also tested
 * @author mark
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAddRemoveFeed {

	private FeedAggregator aggregator;
	
	@Before
	public void setUp() throws Exception {
		this.aggregator = FeedAggregator.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		this.aggregator.shutdown();
	}

	/**
	 * Include a few feeds
	 */
	@Test
	public void test1AddFeeds() {
		fail("Not yet implemented");
		
	}

	/**
	 * Ensure that data was fetch for the existing Feeds
	 */
	@Test
	public void test2RefreshFeeds() {
		fail("Not yet implemented");
	}

	/**
	 * Remove the Feeds 
	 */
	@Test
	public void test3RemoveFeeds() {
		fail("Not yet implemented");
	}

}
