package org.apretaste.test;

import static org.junit.Assert.*;

import org.apretaste.rss.PropertiesReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Properties Reader class
 * @author mark
 *
 */
public class TestProperties {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1ReadBasic() {
		assertNotNull(PropertiesReader.getInstance().getProperties());
		
		assertTrue(!PropertiesReader.getInstance().getCategories().isEmpty());
		
		assertTrue(!PropertiesReader.getInstance().getCategories().first().getFeeds().isEmpty());
	}

}
