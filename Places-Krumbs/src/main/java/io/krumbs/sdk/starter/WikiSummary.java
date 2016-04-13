package io.krumbs.sdk.starter;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WikiSummary {
	
	public static String getSummary(String query) throws IOException, ParseException{
		
		String google_url = Wikipedia.googleCall(query);
		
		String wiki_query = getWikiQuery(google_url);
		String wiki_api_call = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";
		wiki_api_call+=wiki_query;
		wiki_api_call+="&format=json";
		String result = Geocoding.makeConnection(wiki_api_call, "");
		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(result);
		String info = json.get(2).toString();
		info = info.substring(2);
		info = info.substring(0, info.length()-2);
		return Wikipedia.removeUnicode(info);

	}
	
	protected static String getWikiQuery(String google_url) throws IOException{
		
		Document doc = Jsoup.connect(google_url).userAgent("Chrome 41.0.2228.0").get();
		String content = doc.toString();
		int first = content.indexOf("https://en.wikipedia.org/wiki");
		content = content.substring(first+30);
		int last = content.indexOf("&amp");
		content = content.substring(0,last);
		
		return content;
	}
	
	public static String getWikiImage(String query) throws IOException{
		
		String google_url = Wikipedia.googleCall(query);
		String wiki_query =getWikiQuery(google_url);
		String wiki_url = "https://en.wikipedia.org/wiki/" + wiki_query;
		
		Document doc = Jsoup.connect(wiki_url).userAgent("Chrome 41.0.2228.0").get();
		Element link = doc.select("img").first();
		String image_url = "https:" + link.attr("src");
		return image_url;
	}

	public static void main(String[] args) throws IOException, ParseException {
		
		System.out.println(getSummary("University of California, Irvine  (UCI)")); // testing API
		System.out.println(getWikiImage("University of California, Irvine  (UCI)"));

	}

}
