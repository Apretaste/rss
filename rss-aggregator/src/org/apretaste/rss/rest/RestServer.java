package org.apretaste.rss.rest;

import org.apretaste.rss.ContentTranslator;
import org.apretaste.rss.FeedAggregator;
import org.apretaste.rss.FeedDownloader;
import org.apretaste.rss.PropertiesReader;
import org.apretaste.rss.model.IPropertiesDefault;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Rest Server for the RSS Aggregator
 * @author mark
 *
 */
public class RestServer implements IPropertiesDefault {

	private static final Logger logger = LoggerFactory.getLogger(RestServer.class);
	
	public void start() {
	    // Create a new Restlet component and add a HTTP server connector to it
		int port = PropertiesReader.getInstance().getIntProperty(REST_PORT);
		String address = PropertiesReader.getInstance().getProperty(REST_ADDRESS);
	    Component component = new Component();
	    Server server;
	    
	    if (address==null || address.isEmpty()) {
			//Listen on all ports
	    	Context context = component.getContext().createChildContext();

			server = new Server(
					context,
					Protocol.HTTP,
					port
					);
		} else {
			//Listen only on the specified address
			Context context = component.getContext().createChildContext();
			server = new Server(
					context,
					Protocol.HTTP,
					address,
					port,
					null
					);
		}
	    
	    //component.getServers().add(Protocol.HTTP, port);
	    component.getServers().add(server);

	    // Attach basic services
	    component.getDefaultHost().attach("/category", GetCategories.class);
	    component.getDefaultHost().attach("/category/{category}", GetCategories.class);
	    component.getDefaultHost().attach("/feed/full/{link}", GetFullFeed.class);
	    component.getDefaultHost().attach("/feed/{category}", GetFeeds.class);
	    component.getDefaultHost().attach("/feed", GetFeeds.class);
	    component.getDefaultHost().attach("/article/{link}", GetArticle.class);

	    // Now, let's start the component!
	    // Note that the HTTP server connector is also automatically started.
	    try {
			component.start();
			
			if (address==null || address.isEmpty()) {
				logger.info("REST Server listening on port " + port);
			} else {
				logger.info("REST Server listening on address " + address + ":" + port);
			}
		} catch (Exception e) {
			logger.error("Could not Start the RESTful Server", e);
			
			throw new RuntimeException(e);
		}
	}
}
