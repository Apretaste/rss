package org.apretaste.test.rss;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.HttpURL;
import org.apretaste.rss.FeedDownloader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.FeedFormat;
import yarfraw.core.datamodel.YarfrawException;
import yarfraw.io.FeedReader;

/**
 * Generic Tests to ensure that the RSS library behaves as expected
 * @author mark
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRssLibrary {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test1FecthRSS_Espn() throws URISyntaxException, YarfrawException, MalformedURLException, IOException {
		String feedUrl = "http://www.espnfc.com/rss";
		//Download Feed
		File file = FeedDownloader.downloadtoTempFile(new URL(feedUrl));

		assertTrue("Empty Feed", file.length()>100);

		//Parse Feed
		try {
			FeedReader reader = new FeedReader(new HttpURL(feedUrl));

			ChannelFeed channel = reader.readChannel();

			assertTrue(reader.getFormat() == FeedFormat.RSS20);
			assertTrue("Empty Feed", !channel.getItems().isEmpty());
		} finally {
			file.delete();
		}

	}
	
	@Test
	public void test2FecthRSS_MartiNoticias() throws URISyntaxException, YarfrawException, MalformedURLException, IOException {
		String feedUrl = "http://www.martinoticias.com/api/epiqq";
		//Download Feed
		File file = FeedDownloader.downloadtoTempFile(new URL(feedUrl));

		assertTrue("Empty Feed", file.length()>100);

		//Parse Feed
		try {
			FeedReader reader = new FeedReader(new HttpURL(feedUrl));

			ChannelFeed channel = reader.readChannel();

			assertTrue(reader.getFormat() == FeedFormat.RSS20);
			assertTrue("Empty Feed", !channel.getItems().isEmpty());
		} finally {
			file.delete();
		}

	}

	@Test
	public void test3FecthRSS_TheGuardianMusicBlog() throws URISyntaxException, YarfrawException, MalformedURLException, IOException {
		String feedUrl = "http://www.theguardian.com/music/musicblog/rss";
		//Download Feed
		File file = FeedDownloader.downloadtoTempFile(new URL(feedUrl));

		assertTrue("Empty Feed", file.length()>100);

		//Parse Feed
		try {
			FeedReader reader = new FeedReader(new HttpURL(feedUrl));

			ChannelFeed channel = reader.readChannel();
			
			assertTrue(reader.getFormat() == FeedFormat.RSS20);
			assertTrue("Empty Feed", !channel.getItems().isEmpty());
		} finally {
			file.delete();
		}

	}

}
