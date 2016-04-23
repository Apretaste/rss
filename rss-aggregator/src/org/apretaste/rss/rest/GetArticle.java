package org.apretaste.rss.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apretaste.rss.ContentTranslator;
import org.apretaste.rss.FeedAggregator;
import org.apretaste.rss.model.Article;
import org.apretaste.rss.model.NewsService;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Return one full Article
 * @author mark
 *
 */
public class GetArticle extends NewsService {

	@Get("json")
	public Article doGet() {
		String articleLink = this.getAttribute("link");
		try {
			articleLink = URLDecoder.decode(articleLink, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Article article = FeedAggregator.getInstance().getArticleByLinkOrID(articleLink);
		
		//Translate Article is needed
		if (article!=null && this.hasLanguageOption() && !article.getFeed().getLanguage().equalsIgnoreCase(this.getLanguageOption())) {
			ContentTranslator translator = ContentTranslator.getInstance();
			translator.translate(article, this.getLanguageOption());
		}
		
		return article;
	}
}
