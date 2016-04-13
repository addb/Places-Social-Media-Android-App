package io.krumbs.sdk.starter;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Wikipedia {

	public static String formatUrl(ArrayList<String> addresses) throws IOException{
		
		String query = addresses.get(0);
		String google_url = googleCall(query);
		String wiki_query =getWikiQuery(google_url, addresses);
		String wiki_api_call = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";
		wiki_api_call+=wiki_query;
		wiki_api_call+="&format=json";
		return wiki_api_call;
	}
	
	public static String getWikiQuery(String google_url, ArrayList<String> addresses) throws IOException{
		
		String query = addresses.get(0);
		Document doc = Jsoup.connect(google_url).userAgent("Chrome 41.0.2228.0").get();
		String content = doc.toString();
		int first = content.indexOf("https://en.wikipedia.org/wiki");
		if(first==-1){
			query = addresses.get(1);
			google_url = googleCall(query);
			doc = Jsoup.connect(google_url).userAgent("Chrome 41.0.2228.0").get();
			content = doc.toString();
			first = content.indexOf("https://en.wikipedia.org/wiki");
		}
		content = content.substring(first+30);
		int last = content.indexOf("&amp");
		content = content.substring(0,last);
		
		return content;
	}
	
	public static String getWikiImage(ArrayList<String> addresses) throws IOException{
		
		String query = addresses.get(0);
		String google_url = googleCall(query);
		String wiki_query =getWikiQuery(google_url, addresses);
		String wiki_url = "https://en.wikipedia.org/wiki/" + wiki_query;
		
		Document doc = Jsoup.connect(wiki_url).userAgent("Chrome 41.0.2228.0").get();
		Element link = doc.select("img").first();
		String image_url = "https:" + link.attr("src");
		return image_url;
	}
	
	public static String makeCall(ArrayList<String> addresses) throws IOException, ParseException{
		
		String wiki_api_call = formatUrl(addresses);
		String result = Geocoding.makeConnection(wiki_api_call, "");
		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(result);
		String info = json.get(2).toString();
		info = info.substring(2);
		info = info.substring(0, info.length()-2);
		return removeUnicode(info);
	}
	
	public static String removeUnicode(String info){
		
		String result ="";
		for(String word: info.split(" ")){
			if(word.contains("\\") && word.contains("u")){
				word = word.replace("\\","");
				String[] arr = word.split("u");
				String text = arr[0];
				for(int i = 1; i < arr.length; i++){
					String append = arr[i].substring(0, 4);
				    int hexVal = Integer.parseInt(append, 16);
				    text += (char)hexVal;
				    if(arr[i].length()>4){
				    	text+=arr[i].substring(4);
				    }
				}
				result+=text;
			}
			else
				result+=word;
			result += " ";
		}
		
		return result;
	}
	
	public static String googleCall(String query){
		
		query+=" wikipedia";
		String url = "http://www.google.com/search?q=";
		query = query.replaceAll(" ", "+");
		query = query.replaceAll(",", "%2C");
		url+=query;
		return url;
	}
	
	
	public static void main(String[] args) throws IOException, ParseException {
		
		ArrayList<String> addresses = Geocoding.getAddress(36.479938, -117.092747);
		System.out.println(addresses);
		System.out.println(makeCall(addresses));
		System.out.println(getWikiImage(addresses));
	}

}
