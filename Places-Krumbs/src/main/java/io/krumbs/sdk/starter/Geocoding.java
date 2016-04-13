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

public class Geocoding {

	/* Get the formatted address by making a call to reverse geocoding api*/
	
	static String android_api_key = "";   // Enter API key 
	
	public static ArrayList<String> getAddress(double lati, double longi) throws IOException, ParseException{

		ArrayList<String> url = getUrlFormatted(lati, longi);
		String api_call = url.get(0);
		String query = url.get(1);
		
		System.out.println(api_call+query);
		
		String result = makeConnection(api_call, query);
		
		ArrayList<String> addresses = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		JSONArray JO = (JSONArray) json.get("results");
		for(int i=0; i<JO.size(); i++){
			JSONObject jo = (JSONObject) JO.get(i);
			String address = (String) jo.get("formatted_address");
			addresses.add(address);
		}
		
		return addresses;
	}
	
	public static ArrayList<String> getUrlFormatted(double lati, double longi){
		
		
		String latlong = Double.toString(lati) + "," + Double.toString(longi);
		String api_call = "https://maps.googleapis.com/maps/api/geocode/json";
		String query = "?latlng=" + latlong+ "&key=" + android_api_key;
		
		ArrayList<String> url = new ArrayList<String>();
		url.add(api_call);
		url.add(query);
		
		return url; 
	}
	
	
	public static String makeConnection(String api_call, String query) throws IOException{
		
		URL url = new URL(api_call + query);
		
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Chrome 41.0.2228.0"); 
		//con.setDoOutput(true);
		con.setDoInput(true); 
		
		/*
		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(query);
		output.close();
		*/
		
		DataInputStream input = new DataInputStream(con.getInputStream()); 
		String result = "";
		for( int c = input.read(); c != -1; c = input.read() ){
			result+= (char)c;
		}
		input.close(); 
		return result;
	}
	
	public static ArrayList<Double> getLatLong(String Address) throws IOException, ParseException{
		
		ArrayList<String> address = new ArrayList<String>();
		for(String word: Address.split(" ")){
			address.add(word.replaceAll("[^a-zA-Z0-9]+", ""));
		}
		
		String api_call = "https://maps.googleapis.com/maps/api/geocode/json?key=" + android_api_key;
		String query = "&address=";
		for(int i=0; i<address.size(); i++){
			query+=address.get(i);
			if(i!=address.size()-1)
				query+="+";
		}
		
		String result = makeConnection(api_call, query);
		
		ArrayList<Double> answer =  new ArrayList<Double>();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		JSONArray JO = (JSONArray) json.get("results");
		
		for(int i=0; i<JO.size(); i++){
			JSONObject jo = (JSONObject) JO.get(i);
			JSONObject a = (JSONObject) jo.get("geometry");
			JSONObject location = (JSONObject) a.get("location");
			Double lat = (Double) location.get("lat");
			Double longi = (Double) location.get("lng");
			answer.add(lat);
			answer.add(longi);
		}
	
		return answer;
	}

	public static void main(String[] args) throws IOException, ParseException{
		
		
//		System.out.println(getAddress(49.566214,11.196317));
		System.out.println(getLatLong("3900, Parkview Lane, Irvine,,, CA")); // testing
