package io.krumbs.sdk.starter;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FourSquareSearch {
	
	String url,name;
	JSONArray address;

	public static ArrayList<FourSquareSearch>  getSearchPlace(Double lati, Double longi) throws IOException, ParseException{
		
		String fs_api_call = getFormattedUrl(lati, longi, "search", "5");
		String result = Geocoding.makeConnection(fs_api_call, "");
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		JSONObject response = (JSONObject) json.get("response");
		JSONArray venues = (JSONArray) response.get("venues");
		ArrayList<FourSquareSearch> fs_venues = new ArrayList<FourSquareSearch>(); 
		for(int i=0; i<venues.size(); i++){
			FourSquareSearch fv = new FourSquareSearch();
			JSONObject venue = (JSONObject) venues.get(i);
			if(venue.get("url")!=null){
				String url = venue.get("url").toString();
				fv.url = url;
			}
			else
				fv.url = null;
			fv.name = venue.get("name").toString();
			JSONObject location = (JSONObject) venue.get("location");
			JSONArray address = (JSONArray) location.get("formattedAddress");
			fv.address = address;
			fs_venues.add(fv);
			fv.print();
		}
		
		return fs_venues;
	}
	
	protected static String getFormattedUrl(Double lati, Double longi, String type, String limit){
		
		String client_id = "";   // enter your client id here
		String client_secret = "";     // enter your client secret here
		String fs_api_call = "https://api.foursquare.com/v2/venues/";
		fs_api_call += type+ "?ll=";
		fs_api_call+= lati.toString() + ",";
		fs_api_call+=longi.toString();
		fs_api_call+= "&client_id=" + client_id;
		fs_api_call+= "&client_secret=" + client_secret;
		fs_api_call+= "&v=20160311";
		fs_api_call+= "&limit=";
		fs_api_call+= limit;
		
		return fs_api_call;
	}
	
	public void print(){
		
		System.out.println("name is:" + this.name);
		System.out.println("url is:" + this.url);
		System.out.println("address is:" + this.address);
		System.out.println();
		System.out.println("****************************");
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
		ArrayList<FourSquareSearch> places =  getSearchPlace(33.639078, -117.840437);
		FourSquareSearch place = places.get(0);
		String name = place.name;
		System.out.println(WikiSummary.getSummary(name));

	}

}
