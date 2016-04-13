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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ADDB Inc on 13-03-2016.
 */
public class FourSquareActivity extends AppCompatActivity implements LocationListener {

    public static String city;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    double lat, lon;
    ArrayList<FourSquareExplore>response=new ArrayList<>();
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 123;
    ProgressDialog mProgressDialog;
    String call;
    public FourSquareActivity(){

    }
    public FourSquareActivity(double lat, double lon){
        this.lat=lat;
        this.lon=lon;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.foursquare_business);

//recycler view to generate cards
        mRecyclerView = (RecyclerView) findViewById(R.id.foursquareRecycle);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressDialog = new ProgressDialog(FourSquareActivity.this);
        mProgressDialog.setMessage("Please wait! Fetching Data from FourSquare Explore API...");
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
            //txt.setText(street + "  |  " + city + "  |  " + countryName);
            FourSquareActivity fa=new FourSquareActivity(lat,lon);
           // response=
                    new FourSquareExplore().execute(fa);//.get();

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
    class FourSquareExplore extends AsyncTask<FourSquareActivity, String, ArrayList<FourSquareExplore>> {

        String name, phone, url, time_status, rating;
        JSONArray address;
        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
            /*pbar=(ProgressBar)findViewById(R.id.pbarmain);
            pbar.setVisibility(View.VISIBLE);*/
        }
        public FourSquareExplore(){
            String name, phone, url, time_status, rating;
        }
        @Override
        protected ArrayList<FourSquareExplore> doInBackground(FourSquareActivity... params) {
            try {
                return getPlaces(params[0].lat, params[0].lon);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<FourSquareExplore> getPlaces(Double lati, Double longi) throws IOException, org.json.simple.parser.ParseException, org.json.JSONException {

            String fs_api_call = getFormattedUrl(lati, longi, "explore", "15");
            System.out.println("lat"+lati+" lon"+longi);
            String result = makeConnection(fs_api_call, "");
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(result);
            JSONObject response = (JSONObject) json.get("response");
            JSONArray groups = (JSONArray) response.get("groups");
            JSONObject group = (JSONObject) groups.get(0);
            JSONArray items = (JSONArray) group.get("items");
            ArrayList<FourSquareExplore> fe_venues = new ArrayList<FourSquareExplore>();
            for (int i = 0; i < items.size(); i++) {

                FourSquareExplore fv = new FourSquareExplore();
                JSONObject place = (JSONObject) items.get(i);
                JSONObject venue = (JSONObject) place.get("venue");

                if (venue.get("name") != null)
                    fv.name = venue.get("name").toString();
                else
                    fv.name = null;

                JSONObject contact = (JSONObject) venue.get("contact");
                if (contact.get("formattedPhone") != null)
                    fv.phone = contact.get("formattedPhone").toString();
                else
                    fv.phone = null;

                JSONObject location = (JSONObject) venue.get("location");
                if (location.get("formattedAddress") != null)
                    fv.address = (JSONArray) location.get("formattedAddress");
                else
                    fv.address = null;

                if (venue.get("url") != null)
                    fv.url = venue.get("url").toString();
                else
                    fv.url = null;

                if (venue.get("rating") != null)
                    fv.rating = venue.get("rating").toString();
                else
                    fv.rating = null;

                JSONObject hours = (JSONObject) venue.get("hours");
                if (hours!=null && hours.get("status") != null)
                    fv.time_status = hours.get("status").toString();
                else
                    fv.time_status = null;

                fe_venues.add(fv);
                fv.print();
            }

            return fe_venues;
        }
        protected String getFormattedUrl(Double lati, Double longi, String type, String limit){

            String client_id = ""; // enter your client id here
            String client_secret = "";    // enter your client secret here
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
        public void print() {

            System.out.println("name is:" + this.name);
            System.out.println("url is:" + this.url);
            System.out.println("time_status is:" + this.time_status);
            System.out.println("rating is:" + this.rating);
            System.out.println("address is:" + this.address);
            System.out.println();
            System.out.println("****************************");
            System.out.println();
        }
        public String makeConnection(String api_call, String query) throws IOException{

            URL url = new URL(api_call + query);

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Chrome 41.0.2228.0");
            con.setDoInput(true);

		

            DataInputStream input = new DataInputStream(con.getInputStream());
            String result = "";
            for( int c = input.read(); c != -1; c = input.read() ){
                result+= (char)c;
            }
            input.close();
            return result;
        }
        @Override
        protected void onPostExecute(ArrayList<FourSquareExplore> result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            print();
            mAdapter = new FourSquareCardAdapter(result);

            mRecyclerView.setAdapter(mAdapter);
            //Log.i("MyResponse",result);
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTime_status() {
            return time_status;
        }

        public void setTime_status(String time_status) {
            this.time_status = time_status;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public JSONArray getAddress() {
            return address;
        }

        public void setAddress(JSONArray address) {
            this.address = address;
        }
    }


}