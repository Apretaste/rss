package org.apretaste.rss;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tika.language.LanguageIdentifier;
import org.apretaste.rss.model.Article;
import org.apretaste.rss.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.ItemEntry;
import yarfraw.core.datamodel.Person;
import yarfraw.core.datamodel.YarfrawException;
import yarfraw.io.CachedFeedReader;
import yarfraw.io.FeedReader;

/**
 * Feed Download module, used for creating RSS/Atom Feed readers
 * @author mark
 *
 */
public class FeedDownloader {

	private static final Logger logger = LoggerFactory.getLogger(FeedDownloader.class);

	/**
	 * Map of Cached Readers, these instances must be kept so they maintain state information
	 * on last update (ETag) of feeds
	 */
	private Map<String, CachedFeedReader> cachedReaders = new Hashtable<>();

	/**
	 * A singleton to control all Feed download and parsing
	 */
	private static FeedDownloader singleton;

	/**
	 * Date parsers for all possible Date Formats
	 */
	private Set<SimpleDateFormat> dateFormats = new HashSet<>(); 

	public static synchronized FeedDownloader getInstance() {
		if (singleton == null) {
			singleton = new FeedDownloader();
		}
		return singleton;
	}

	private FeedDownloader() {
		//Include all Date formats commonly used in RSS feeds 
		this.dateFormats.add(new SimpleDateFormat("EEEE, dd MMMM yyyy kk:mm:ss"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		this.dateFormats.add(new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy"));
		this.dateFormats.add(new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ"));
		this.dateFormats.add(new SimpleDateFormat("MMM dd, yyyy hh:mm a"));
		this.dateFormats.add(new SimpleDateFormat("MMM d yyyy  hh:mm a"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mmZ"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		this.dateFormats.add(new SimpleDateFormat("yyyy-MM-dd"));
	}

	/**
	 * Fetch Contents of remote file
	 * @param _url
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public static byte[] download(URL _url) throws IOException, URISyntaxException {
		HttpRequestBase request = new HttpGet(_url.toURI());
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(request);

		return EntityUtils.toByteArray(response.getEntity());
	}

	/**
	 * Download the contents of a URL to a local file
	 * the file is generated under the default TEMP folder
	 * and is set ot be delete upon the JVM termination
	 * @param _url
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public static File downloadtoTempFile(URL _url) throws IOException, URISyntaxException {
		//Download contents
		byte[] data = download(_url);

		//Save to local file
		File file = File.createTempFile("feed_", ".xml");
		file.deleteOnExit();

		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(data), file);

		return file;
	}

	/**
	 * Returns an existing Cached Reader, if one exists,
	 * otherwise a new instance is returned
	 * @param _link
	 * @return
	 * @throws IOException 
	 * @throws YarfrawException 
	 * @throws URIException 
	 */
	private synchronized FeedReader getReaderForLink(String _link) throws URIException, YarfrawException, IOException {
		CachedFeedReader cachedReader = this.cachedReaders.get(_link);

		//Create a new instance and cache it
		if (cachedReader == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating new RSS Cached Reader for link: " + _link);
			}
			
			cachedReader = new CachedFeedReader(new HttpURL(_link));
			
			this.cachedReaders.put(_link, cachedReader);
		}

		return cachedReader;
	}

	/**
	 * Trim string values that are larger than the length acceptable
	 * @param _value Value to potentially trim
	 * @param _length Max length
	 * @return
	 */
	public static String fieldTrim(String _value, int _length) {
		String value = _value;
		if (value!=null) {
			value = value.trim();
			
			if (value.length()>_length) {
				value = value.substring(0, _length);
			}
		}
		
		return value;
	}
	
	/**
	 * Fetch and parse a RSS/Atom feed online
	 * @param _link URL for RSS/Atom Feed
	 * @return
	 */
	public Feed fetchOnlineFeed(String _link) {
		Feed feed = new Feed();
		feed.setLink(_link);

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Parsing Feed at: " + _link + " - START");
			}
			
			FeedReader cachedReader = this.getReaderForLink(_link);

			ChannelFeed channel = cachedReader.readChannel();

			//Gather Feed Information
			feed.setTitle(fieldTrim(channel.getTitleText(),255));
			feed.setDescription(fieldTrim(channel.getDescriptionOrSubtitleText(), 2048));
			feed.setLanguage(channel.getLang());
			feed.setCopyright(channel.getRightsText());
			
			//Parse Pub Date
			if (channel.getPubDate()!=null && !channel.getPubDate().isEmpty()) {
				for (SimpleDateFormat dateParser : dateFormats) {
					try {
						feed.setPubDate(dateParser.parse(channel.getPubDate()));

						//Good format
						break;
					} catch (ParseException e) {
						//Not yet, keep trying
					}
				}
			}

			//If no Pub date is not set, use Last Build Date
			if (feed.getPubDate()==null) {
				//Try using the lastBuildDate
				for (SimpleDateFormat dateParser : dateFormats) {
					try {
						feed.setPubDate(dateParser.parse(channel.getLastBuildOrUpdatedDate()));

						//Good format
						break;
					} catch (Exception e) {
						//Not yet, keep trying
					}
				}
			}
			
			//Still no date, use current Date
			if (feed.getPubDate()==null) {
				logger.warn("Unrecognizable Date Format in Feed '" + _link + "': " + channel.getPubDate());
				
				feed.setPubDate(new Date());
			}

			//Parse feed and create object representation
			int count = 0;
			if (channel.getItems()!=null && !channel.getItems().isEmpty()) {
				for (ItemEntry entry : channel.getItems()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parsing Feed Entry (" + count + "): " + entry.getTitleText());
					}

					Article art = new Article();
					art.setTitle(fieldTrim(entry.getTitleText(), 255));
					art.setDescription(entry.getDescriptionOrSummaryText());

					if (entry.getUid()!=null) {
						art.setGuid(entry.getUid().getIdValue());
					}

					//Get the first link included in the Article
					if (entry.getLinks()!=null && !entry.getLinks().isEmpty()) {
						art.setLink(entry.getLinks().get(0).getHref());
					}

					//Include all Authors
					if (entry.getAuthorOrCreator()!=null && !entry.getAuthorOrCreator().isEmpty()) {
						StringBuffer sbAuthor = new StringBuffer();
						for (Person author : entry.getAuthorOrCreator()) {
							if (author.getName()!=null) {
								sbAuthor.append(author.getName());
								sbAuthor.append(", ");
							}
						}

						//Remove last comma
						if (sbAuthor.length()>2) {
							sbAuthor.substring(0,sbAuthor.length()-2);
						}

						art.setAuthor(sbAuthor.toString().trim());
					}

					//Parse Pub Date
					if (entry.getPubDate()!=null && !entry.getPubDate().isEmpty()) {
						for (SimpleDateFormat dateParser : dateFormats) {
							try {
								art.setPubDate(dateParser.parse(entry.getPubDate()));

								//Good format
								break;
							} catch (Exception e) {
								//Not yet, keep trying
							}
						}
					}

					if (art.getPubDate()==null) {
						logger.warn("Unrecognizable Date Format in Article '" + art.getTitle() + "': " + entry.getPubDate());

						//Use the same date as feed
						art.setPubDate(feed.getPubDate());
					}

					//Try really hard to remove extra empty spaces on article descriptions
					if (art.getDescription() !=null && !art.getDescription().isEmpty()) {
						//Remove windows style carriage return at end of line
						while (art.getDescription().indexOf("\r")!=-1) {
							art.setDescription(art.getDescription().replaceAll("\r", "\n"));
						}
						//Replace tabs with single space
						while (art.getDescription().indexOf("\t")!=-1) {
							art.setDescription(art.getDescription().replaceAll("\t", " "));
						}
						//Remove double spaces
						while (art.getDescription().indexOf("  ")!=-1) {
							art.setDescription(art.getDescription().replaceAll("  ", " "));
						}
						//Remove empty spaces before a new line
						while (art.getDescription().indexOf(" \n")!=-1) {
							art.setDescription(art.getDescription().replaceAll(" \n", "\n"));
						}

						//Remove double line ends
						while (art.getDescription().indexOf("\n\n")!=-1) {
							art.setDescription(art.getDescription().replaceAll("\n\n", "\n"));
						}

						//Avoid too long descriptions 
						art.setDescription(fieldTrim(art.getDescription(), 2048));
					}

					//Add article to feed
					feed.addArticle(art);

					if (logger.isDebugEnabled()) {
						logger.debug("Article Parsed ---> " + art.toString());
					}

					count++;
				}
			} else {
				//Feed is empty
				logger.info("Feed has no articles, Title: " + feed.getTitle() + ", Link: " + feed.getLink());
			}

			//Re-visit Language code for corrections and auto detection
			if (feed.getLanguage()==null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No Language set for Feed '" + feed.getTitle() + "', using Apache Tika to Detect Language");
				}
				
				//Use Apache Tika to find out the language code
				StringBuffer sbArticleText = new StringBuffer(2048); 
				for (Article art : feed.getArticles()) {
					sbArticleText.append(art.getTitle());
					sbArticleText.append(' ');
					sbArticleText.append(art.getDescription());
					sbArticleText.append(' ');
				}
				
				LanguageIdentifier identifier = new LanguageIdentifier(sbArticleText.toString());
				feed.setLanguage(identifier.getLanguage());
				
				if (logger.isDebugEnabled()) {
					logger.debug("Language detected as being '" + feed.getLanguage() + "' for Feed '" + feed.getTitle() + "'");
				}
			} else {
				//Ensure it only used ISO's 2 letter standard
				if (feed.getLanguage().length()>2) {
					feed.setLanguage(feed.getLanguage().substring(0, 2));
				}
			}

		} catch (YarfrawException | IOException e) {
			e.printStackTrace();
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("Parsed Feed at: " + _link + " - END ---> " + feed.toString());
			}
		}

		return feed;
	}
}
