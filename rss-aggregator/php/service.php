<?php

include('httpful.phar');
require_once 'htmlpurifier/HTMLPurifier.auto.php';
require_once 'articlecleaner.php';

class Noticias extends Service
{
	/**
	 * Function executed when the service is called
	 * 
	 * @param Request
	 * @return Response
	 * */
	public function _main(Request $request)
	{
		//Service Base URL
		$url = "http://127.0.0.1:8182";
		
		//Article Description Cleaner
		$cleaner = new ArticleCleaner();

		//If no command, show list of Categories and help information
		if (empty($request->query)) {
			$urlCat = $url . "/category?lang=es";
			$responseCat = \Httpful\Request::get($urlCat)
			->expectsJson()
			->send();
			
			// create a json object to send to the template
			$responseContent = array(
				"categories" => $responseCat->body
			);
	
			// create the response
			$response = new Response();
			$response->setResponseSubject("NOTICIAS Lista de Categorias y Como Utilizar el Servicio");
			$response->createFromTemplate("category.tpl", $responseContent);
			return $response;
		} else if (substr($request->query,0,3)=="/__") {
			//Show a Full Feed from a Category
			$feedLink = urlencode(substr($request->query,3));
			
			//Retrieve All Categories
			$urlAllCat = $url . "/category?lang=es";
			$responseAllCat = \Httpful\Request::get($urlAllCat)
			->expectsJson()
			->send();
			
			//Retrieve Full Feed
			$urlFeed = $url . "/feed/full/" . $feedLink . "?lang=es";
			$responseFeed = \Httpful\Request::get($urlFeed)
			->expectsJson()
			->send();
			
			//Allow 4000 characters per article and 100000 per feed
			$feed = $cleaner->cleanFeed($responseFeed->body, 4000, 100000);
			
			// create a json object to send to the template
			$responseContent = array(
					"categories" => $responseAllCat->body,
					"feed" => $feed
			);
				
			// create the response
			$response = new Response();
			$response->setResponseSubject("[RESPONSE_EMAIL_SUBJECT]");
			$response->createFromTemplate("fullFeed.tpl", $responseContent);
			return $response;
		} else if (substr($request->query,0,3)==">__" or substr($request->query,0,3)==">>_") {
			//Show Full Article
			$articleLink = urlencode(substr($request->query,3));
			
			//Which language to use in displaying the Article
			$articleLanguage = (substr($request->query,0,3)==">>_") ? "" : "es";
				
			//Retrieve All Categories
			$urlAllCat = $url . "/category?lang=es";
			$responseAllCat = \Httpful\Request::get($urlAllCat)
			->expectsJson()
			->send();
				
			//Retrieve Full Feed
			$urlArticle = $url . "/article/" . $articleLink . "?lang=" . $articleLanguage;
			$responseArticle = \Httpful\Request::get($urlArticle)
			->expectsJson()
			->send();
			
			//Clean Article Description
			$article = $cleaner->cleanArticle($responseArticle->body);
			
			// create a json object to send to the template
			$responseContent = array(
					"categories" => $responseAllCat->body,
					"article" => $article
			);
			
			// create the response
			$response = new Response();
			$response->setResponseSubject("[RESPONSE_EMAIL_SUBJECT]");
			$response->createFromTemplate("fullArticle.tpl", $responseContent);
			return $response;
		} else {
			//List Feeds under a Category
			$catTitle = $request->query;
			
			$urlAllCat = $url . "/category?lang=es";
			$responseAllCat = \Httpful\Request::get($urlAllCat)
			->expectsJson()
			->send();

			$urlCat = $url . "/category/" . $catTitle . "?lang=es";
			$responseCat = \Httpful\Request::get($urlCat)
			->expectsJson()
			->send();
				
			$urlFeeds = $url . "/feed/" . $catTitle . "?lang=es";
			$responseFeeds = \Httpful\Request::get($urlFeeds)
			->expectsJson()
			->send();
			
			//Clean Article Contents in Feeds
			$feeds = $responseFeeds->body;
			foreach ($feeds as $feed) {
				//Allow 2000 characters per article and 25000 per feed
				$cleaner->cleanFeed($feed, 2000, 25000);
			}
			
			// create a json object to send to the template
			$responseContent = array(
				"category" => $responseCat->body,
				"categories" => $responseAllCat->body,
				"feeds" => $feeds
			);
			
			// create the response
			$response = new Response();
			$response->setResponseSubject("[RESPONSE_EMAIL_SUBJECT]");
			$response->createFromTemplate("topFeeds.tpl", $responseContent);
			return $response;
		}
	}
}
