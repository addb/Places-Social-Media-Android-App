package io.krumbs.sdk.starter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FourSquareExplore {

	String name, phone, url, time_status, rating;
	JSONArray address;
	
	public static ArrayList<FourSquareExplore> getPlaces(Double lati, Double longi) throws IOException, ParseException{
		
		String fs_api_call = FourSquareSearch.getFormattedUrl(lati, longi, "explore", "15");
		String result = Geocoding.makeConnection(fs_api_call, "");
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		JSONObject response = (JSONObject) json.get("response");
		JSONArray groups = (JSONArray) response.get("groups");
		JSONObject group = (JSONObject) groups.get(0);
		JSONArray items = (JSONArray) group.get("items");
		ArrayList<FourSquareExplore> fe_venues = new ArrayList<FourSquareExplore>();
		for(int i=0; i<items.size(); i++){
			
			FourSquareExplore fv = new FourSquareExplore();
			JSONObject place = (JSONObject) items.get(i);
			JSONObject venue = (JSONObject) place.get("venue");
			
			if(venue.get("name")!=null)
				fv.name = venue.get("name").toString();
			else
				fv.name = null;
			
			JSONObject contact = (JSONObject) venue.get("contact");
			if(contact.get("formattedPhone")!=null)
				fv.phone = contact.get("formattedPhone").toString();
			else
				fv.phone = null;
			
			JSONObject location = (JSONObject) venue.get("location");
			if(location.get("formattedAddress")!=null)
				fv.address = (JSONArray) location.get("formattedAddress");
			else
				fv.address = null;
			
			if(venue.get("url")!=null)
				fv.url = venue.get("url").toString();
			else
				fv.url = null;
			
			if(venue.get("rating")!=null)
				fv.rating = venue.get("rating").toString();
			else
				fv.rating = null;
			
			JSONObject hours = (JSONObject) venue.get("hours");
			if(hours!=null&&hours.get("status")!=null)
				fv.time_status = hours.get("status").toString();	
			else
				fv.time_status = null;
	
			fe_venues.add(fv);
			fv.print();
		}
		
		return fe_venues;	
	}
	
	public void print(){
		
		System.out.println("name is:" + this.name);
		System.out.println("url is:" + this.url);
		System.out.println("time_status is:" + this.time_status);
		System.out.println("rating is:" + this.rating);
		System.out.println("address is:" + this.address);
		System.out.println();
		System.out.println("****************************");
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub

		//getPlaces(33.646222, -117.842886);
		getPlaces(33.6691253, -117.82906872); //testing

	}

}
