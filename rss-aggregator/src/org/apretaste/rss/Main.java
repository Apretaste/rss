package org.apretaste.rss;

import java.io.IOException;

import org.apretaste.rss.rest.RestServer;

public class Main {

	public static void main(String[] args) throws IOException {
		FeedAggregator agg = FeedAggregator.getInstance();
		
		//Starting sub-systems
		ContentTranslator.getInstance();
		FeedDownloader.getInstance();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		
		//Start Rest Server
		RestServer server = new RestServer();
		server.start();
		
		//Do not end the program until the user types something
		System.in.read();
		
		//Gracefully stop all services
		agg.shutdown();
	}
	
	/**
	 * Print Command Line Usage for this tool
	 */
	public static void printUsage() {
		
	}

}
