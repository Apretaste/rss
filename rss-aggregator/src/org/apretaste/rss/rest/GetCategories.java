package org.apretaste.rss.rest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apretaste.rss.FeedAggregator;
import org.apretaste.rss.model.Category;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Return all categories
 * @author mark
 *
 */
public class GetCategories extends ServerResource {

	@Get("json")
	public Object doGet() {
		String catTitle = this.getAttribute("category");
		
		if (catTitle==null) {
			//All Categories
			List<Map<String, Object>> result = new ArrayList<>();
			
			Set<Category> cats = FeedAggregator.getInstance().getCategories();
			
			//All Categories
			for (Category cat : cats) {
				Map<String, Object> catData = new Hashtable<>();
				
				catData.put("title", cat.getTitle());
				catData.put("description", cat.getDescription());
				catData.put("articleCount", cat.getArticleCount());
				
				result.add(catData);
			}
			
			return result;
		} else {
			//Only Selected Category
			Category cat = FeedAggregator.getInstance().getCategoryByTitle(catTitle);
			
			Map<String, Object> catData = new Hashtable<>();
			
			catData.put("title", cat.getTitle());
			catData.put("description", cat.getDescription());
			catData.put("articleCount", cat.getArticleCount());

			return catData;
		}
		
	}
}
