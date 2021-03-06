package org.apache.tika.language.translate;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.apache.tika.language.translate.Translator;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An implementation of a REST client for the
 * <a href="https://developer.lingo24.com/premium-machine-translation-api">Premium MT API v1</a>.
 * You can sign up for an access plan online on the <a href="https://developer.lingo24.com/plans">Lingo24 Developer Portal</a>
 * and set your Application's User Key in the <code>translator.lingo24.properties</code> file.
 */
public class Lingo24TranslatorHack implements Translator {

	/**
	 * Lingo24 translation service service end-point URL
	 */
	private static final String LINGO24_TRANSLATE_URL_BASE = "https://api.lingo24.com/mt/v1/translate";

	/**
	 * Default USer-Key, a real User-Key must be provided before the Lingo24 can successfully request translations
	 */
	private static final String DEFAULT_KEY = "dummy-key";

	/**
	 * Identifies the client of the request, used for authentication 
	 */
	private String userKey;

	public Lingo24TranslatorHack() {
		Properties config = new Properties();
		try {
			config.load(Lingo24TranslatorHack.class
					.getResourceAsStream(
							"translator.lingo24.properties"));
			this.userKey = config.getProperty("translator.user-key");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String translate(String text, String sourceLanguage,
			String targetLanguage) throws TikaException, IOException {
		if (!this.isAvailable()) {
			return text;
		}
		WebClient client = WebClient.create(LINGO24_TRANSLATE_URL_BASE);

		Response response = client.accept(MediaType.APPLICATION_JSON)
				.query("user_key", this.userKey).query("source", sourceLanguage)
				.query("target", targetLanguage).query("q", text).get();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				(InputStream) response.getEntity(), UTF_8));
		String line = null;
		StringBuffer responseText = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			responseText.append(line);
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonResp = mapper.readTree(responseText.toString());
			if (jsonResp.findValuesAsText("errors").isEmpty()) {
				return jsonResp.findValuesAsText("translation").get(0);
			} else {
				throw new TikaException(jsonResp.findValue("errors").get(0).asText());
			}
		} catch (JsonParseException e) {
			throw new TikaException("Error requesting translation from '" + sourceLanguage + "' to '" + targetLanguage + "', JSON response from Lingo24 is not well formatted: " + responseText.toString());
		}
	}

	/**
	 * Retrieve current User-Key
	 * @return User-Key in use
	 */
	public String getUserKey() {
		return userKey;
	}

	/**
	 * Set User-Key for authentication
	 * @param userKey USer-Key for authentication 
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	@Override
	public String translate(String text, String targetLanguage)
			throws TikaException, IOException {
		if (!this.isAvailable())
			return text;
		LanguageIdentifier language = new LanguageIdentifier(
				new LanguageProfile(text));
		String sourceLanguage = language.getLanguage();
		return translate(text, sourceLanguage, targetLanguage);
	}

	@Override
	public boolean isAvailable() {
		return this.userKey!=null && !this.userKey.equals(DEFAULT_KEY);
	}

}
