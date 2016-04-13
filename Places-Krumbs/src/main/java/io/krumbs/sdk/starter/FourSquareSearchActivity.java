package io.krumbs.sdk.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by ADDB Inc on 15-03-2016.
 */
public class FourSquareSearchActivity extends AppCompatActivity implements LocationListener{


    public static String city;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    double lat, lon;
    ArrayList<FourSquareWikiSearch> response=new ArrayList<>();
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 123;
    ProgressDialog mProgressDialog;
    public FourSquareSearchActivity(){

    }
    public FourSquareSearchActivity(double lat, double lon){
        this.lat=lat;
        this.lon=lon;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_foursquare_wiki);
        mRecyclerView = (RecyclerView) findViewById(R.id.foursquarewikiRecycle);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        
        //loading univeral image loader library with caching
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
        //***********************************************************************************
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//recycler view to generate cards

        mProgressDialog = new ProgressDialog(FourSquareSearchActivity.this);
        mProgressDialog.setMessage("Please wait! Fetching Data from FourSquare Search API...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);

            }

            //

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5000, this);

    }

    // location related methods
    @Override
    public void onLocationChanged(Location location) {
       /* TextView txtLat = (TextView) findViewById(R.id.locationText);
        TextView txt = (TextView) findViewById(R.id.nameLocation);

        txtLat.setText("Lat:" + location.getLatitude() + "\nLong:" + location.getLongitude());*/
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        lat = location.getLatitude();
        lon = location.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            String street = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            // txt.setText(street + "  |  " + city + "  |  " + countryName);
            FourSquareSearchActivity fa=new FourSquareSearchActivity(lat,lon);
            //response=
                    new FourSquareWikiSearch().execute(fa);//.get();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    //if permission denied
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! 

                } else {

                    // permission denied, boo! 
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    //API CALL IN ASYNC TASK
    class FourSquareWikiSearch extends AsyncTask<FourSquareSearchActivity, String, ArrayList<FourSquareWikiSearch>> {

        String name, url,adr, image,summary;
        JSONArray address;

        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
        }
        public FourSquareWikiSearch(){
            String name,adr,url, image,summary;
        }
        @Override
        protected ArrayList<FourSquareWikiSearch> doInBackground(FourSquareSearchActivity... params) {
            try {
                return getSearchPlace(params[0].lat, params[0].lon);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        public  ArrayList<FourSquareWikiSearch>  getSearchPlace(Double lati, Double longi) throws IOException, ParseException {

            String fs_api_call = getFormattedUrl(lati,longi, "search", "5");
            String result = Geocoding.makeConnection(fs_api_call, "");
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(result);
            JSONObject response = (JSONObject) json.get("response");
            JSONArray venues = (JSONArray) response.get("venues");
            ArrayList<FourSquareWikiSearch> fs_venues = new ArrayList<FourSquareWikiSearch>();
            for(int i=0; i<1; i++){
                FourSquareWikiSearch fv = new FourSquareWikiSearch();
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

                try {
                    fv.summary=getSummary(fv.name);
                    fv.image =getWikiImage(fv.name);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fs_venues.add(fv);
                fv.print();
            }

            return fs_venues;
        }

        public void print() {

            System.out.println("name is:" + this.name);
            System.out.println("url is:" + this.url);
            System.out.println("address is:" + this.address);
            System.out.println("image is:" + this.image);System.out.println("summary is:" + this.summary);
            System.out.println();
            System.out.println("****************************");
            System.out.println();
        }
        protected String getFormattedUrl(Double lati, Double longi, String type, String limit){
            String client_id = "";        // enter your client id here
            String client_secret = "";      // enter your client secret here
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
        public String getSummary(String query) throws IOException, ParseException{

            String google_url = googleCall(query);

            String wiki_query = getWikiQuery(google_url);
            String wiki_api_call = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";
            wiki_api_call+=wiki_query;
            wiki_api_call+="&format=json";
            String result = Geocoding.makeConnection(wiki_api_call, "");
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(result);
            String info = json.get(2).toString();
            info = info.substring(2);

            info = info.substring(0, info.length() - 2);
            info= removeUnicode(info);
            info=info.replaceAll("[\",\"]", "");
            return info;

        }
        public  String removeUnicode(String info){

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

        public String googleCall(String query){

            query+=" wikipedia";
            String url = "http://www.google.com/search?q=";
            query = query.replaceAll(" ", "+");
            query = query.replaceAll(",", "%2C");
            url+=query;
            return url;
        }

        protected String getWikiQuery(String google_url) throws IOException{

            Document doc = Jsoup.connect(google_url).userAgent("Chrome 41.0.2228.0").get();
            String content = doc.toString();
            int first = content.indexOf("https://en.wikipedia.org/wiki");
            content = content.substring(first+30);
            int last = content.indexOf("&amp");
            content = content.substring(0,last);

            return content;
        }

        public  String getWikiImage(String query) throws IOException{

            String google_url = Wikipedia.googleCall(query);
            String wiki_query =getWikiQuery(google_url);
            String wiki_url = "https://en.wikipedia.org/wiki/" + wiki_query;

            Document doc = Jsoup.connect(wiki_url).userAgent("Chrome 41.0.2228.0").get();
            Element link = doc.select("img").first();
            String image_url = "https:" + link.attr("src");
            return image_url;
        }
        @Override
        protected void onPostExecute(ArrayList<FourSquareWikiSearch> places) {
            super.onPostExecute(places);
            mProgressDialog.dismiss();
           /* places.get(0);
            String name = places.get(0).name;*/

            //print();
           mAdapter = new FourSquareWikiCardAdapter(places);

           mRecyclerView.setAdapter(mAdapter);
            //Log.i("MyResponse",result);
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getAdr() {
            return adr;
        }

        public String getImage() {
            return image;
        }

        public String getSummary() {
            return summary;
        }
    }


}