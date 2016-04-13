package io.krumbs.sdk.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
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
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchableActivity extends AppCompatActivity implements LocationListener {


    public static String city;
    EditText mEdit;

    protected boolean gps_enabled, network_enabled;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String call;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CardContent> cardContent=new ArrayList<>();
    ProgressDialog mProgressDialog;
    static final int MY_PERMISSION_REQUEST_FINE_LOCATION=123;
    double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mRecyclerView = (RecyclerView) findViewById(R.id.searcrecycle);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mProgressDialog = new ProgressDialog(SearchableActivity.this);
        mProgressDialog.setMessage("Please wait! Fetching Data from Eventshop server...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

       
        mEdit=(EditText)findViewById(R.id.userQuery);
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mEdit = (EditText) findViewById(R.id.userQuery);
                    System.out.println("Search bar input here: " + mEdit.getText().toString() + "keyboard button!!");
                    try {
                        ArrayList<Double> ag =Geocoding.getLatLong(mEdit.getText().toString());
                        System.out.println("location: lat:"+ag.get(0)+" lon"+ag.get(1));
                        lat=ag.get(0);
                        lon=ag.get(1);
                        call="http://sln.ics.uci.edu:8085/eventshoplinux/rest/sttwebservice/search/71/circle/"+lat+","+lon+",0.01/"+"1457833742522"+"/null";
                        System.out.println(call);
                        new RequestTask().execute(call);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mEdit.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;

            }

        });
        //displayText = (TextView) findViewById(R.id.searchViewResult);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {

                // No explanation needed, we can request the permission.

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
        /*txtLat = (TextView) findViewById(R.id.locationText);
        txt = (TextView) findViewById(R.id.nameLocation);

        txtLat.setText("Lat:" + location.getLatitude() + "\nLong:" + location.getLongitude());*/
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        double  lat,lon;
        lat=location.getLatitude();
        lon=  location.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            String street=addresses.get(0).getAddressLine(0);
            city=addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            //txt.setText(street + "  |  " + city + "  |  " + countryName);


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

    class RequestTask extends AsyncTask<String, String, ArrayList<CardContent>> {
        String data="";Geocoder geocoder = new Geocoder(SearchableActivity.this, Locale.getDefault());
        ProgressBar pbar;
        ArrayList<CardContent>responseList=new ArrayList<>();

        //RecyclerView mRecyclerView;
        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
        }
        @Override
        protected ArrayList<CardContent> doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            try {
                responseList=parseKrumbsJson(responseString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseList;
        }
        public ArrayList<CardContent> parseKrumbsJson(String kfeed) throws JSONException, IOException {
            String data="";ArrayList<CardContent>response=new ArrayList<>();
            System.out.println("feed->"+kfeed);
            //JSONObject jobject =new JSONObject(kfeed);

            JSONArray jsonArray = new JSONArray(kfeed);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                String y = o.getJSONObject("stt_what").getJSONObject("caption").getString("value");
                System.out.println(y);
                String x = null;
                if (o.getJSONObject("stt_what").has("media_source_photo") && o.getJSONObject("stt_what").getJSONObject("media_source_photo").has("value")) {
                    x = o.getJSONObject("stt_what").getJSONObject("media_source_photo").getString("value");//change to media source_photo if only 1 json response
                    System.out.println("Here: " + x);
                }
                if(jsonArray.length()==1)
                {
                    x = o.getJSONObject("stt_what").getJSONObject("media_source_photo").getString("value");
                }
                String z = o.getJSONObject("stt_what").getJSONObject("intent_emoji_unicode").getString("value");
                Double lat = (Double) o.getJSONObject("stt_where").getJSONArray("point").get(0);
                Double lon = (Double) o.getJSONObject("stt_where").getJSONArray("point").get(1);
                Long time=(Long)o.getJSONObject("stt_when").getLong("datetime");
                //String imgPath = c.getString();
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                String street = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                CardContent c=new CardContent();
                c.setTime(time);
                c.setCaption(y);
                c.setImg_url(x);
                c.setEmoji(z);
                c.setCity(city);
                c.setCountry(countryName);
                response.add(c);// add card content object in the arraylist
            }
            //  CardContent c=new CardContent(x,y);
            Log.i("ResponseReceived", data);
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList<CardContent> result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Collections.sort(result, new Comparator<CardContent>() {

                public int compare(CardContent s1, CardContent s2) {
                    Long time1 = s1.getTime();
                    Long time2 = s2.getTime();
                    return time2.compareTo(time1);
                }
            });
            mAdapter = new CardAdapter(result);

            mRecyclerView.setAdapter(mAdapter);
            //Log.i("MyResponse",result);
        }


    }

}
