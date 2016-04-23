<?php

//
// This flie includes functions helpful in cleaning and improving the HTML present in News Articles
//

class ArticleCleaner {

	/**
	 * HTML Purifier configured and ready for use
	 * @var unknown $purifier
	 */
	private $purifier;
	
	function __construct() {
		$this->purifier = $this->createPurifier();
	}
	
	/**
	 * Create a new Purifier
	 */
	private function createPurifier() {
		//HTML Purifier Initialization
		$config = HTMLPurifier_Config::createDefault();
		//$config->set('Core.Encoding', 'ISO-8859-1'); //Simp[ler encoding for simpler e-mail clients
		$config->set('HTML.Doctype', 'HTML 4.01 Transitional'); //Older standard, better support for outdated e-mail clients
		$config->set('AutoFormat.DisplayLinkURI', true); //Disable links but show their HREF attribute
		$config->set('HTML.ForbiddenElements', array('img'));
		
		//Return fully initialized purifier
		return new HTMLPurifier($config);
	}

	/**
	 * Cleans and compress HTML sniplets
	 * @param unknown $dirty_html
	 * @return Cleaned HTML Code
	 */
	public function cleanHtml($dirty_html) {
		return  $this->purifier->purify($dirty_html);
	}
	
	/**
	 * An Article's HTML content, this method acts only on the Article's description and
	 * simplifies the cleanup process
	 * @param unknown $article
	 * @param integer $maxLength Maximun legnth of each Article Description
	 */
	public function cleanArticle($article, $maxLength = -1) {
		//Clean the article's description is one is available
		if (property_exists($article, "description")) {
			$article->description = $this->cleanHtml($article->description);
			
			if ($maxLength>0 && strlen($article->description)>$maxLength) {
				$article->description = substr($article->description,0,$maxLength-3);
				
				//Clean anything that could have been messed up
				$article->description = $this->cleanHtml($article->description);
				
				$article->description = $article->description . "...";
				
				//Flag Article as trimmed
				$article->trimmed=true;
			} else {
				$article->trimmed=false;
			}
		}
		
		return $article;
	}
	
	/**
	 * Cleans the HTML content of all Articles in a Feed
	 * @param unknown $feed
	 * @param integer $maxLength Maximun legnth of each Article Description
	 * @param integer $totalMaxLength Maximum length of all Article Descriptions, once this limit is reached further articles will have no description
	 */
	public function cleanFeed($feed, $maxLength = -1, $totalMaxLength = -1) {
		if (property_exists($feed, "articles")) {
			//Clean individual Articles
			foreach($feed->articles as $article) {
				$this->cleanArticle($article, $maxLength);
			}
		}
		
		//Ensure that total length of Areticle Descriptions in a the alowed maximum
		$totalLength = 0;
		if ($totalMaxLength > 0) {
			foreach($feed->articles as $article) {
				if ($totalLength > $totalMaxLength) {
					//Over the limit already, Completely remove Descriptions
					$article->description = null;
					$article->trimmed = true;
				} else {
					//Still within the limits set
					$tempLength = $totalLength + strlen($article->description);
					if ($tempLength > $totalMaxLength) {
						//Trim to keep within limit
						$trimLength = $tempLength - $totalMaxLength;
						$this->cleanArticle($article, $trimLength);
					}
				}
				
				//Increase accumulated length
				$totalLength += strlen($article->description);
			}
		}
		
		return $feed;
	}

}