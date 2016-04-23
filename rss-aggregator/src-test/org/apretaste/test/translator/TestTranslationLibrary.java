package org.apretaste.test.translator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.apretaste.rss.ContentTranslator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import yarfraw.core.datamodel.YarfrawException;

/**
 * Generic Tests to ensure that the RSS library behaves as expected
 * @author mark
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTranslationLibrary {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test1EN_ES_Translation() throws URISyntaxException, YarfrawException, MalformedURLException, IOException {
		ContentTranslator translator = ContentTranslator.getInstance();
		
		String testText = "Hello World!!!";
		String transText = translator.translate(testText, "en", "es");
		
		assertNotNull("Text not translated", transText);
		assertNotEquals("Text not translated", testText, transText);
		
	}

}
