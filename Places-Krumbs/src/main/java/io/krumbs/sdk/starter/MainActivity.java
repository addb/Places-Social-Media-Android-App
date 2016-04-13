/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private View cameraView;
    private View startCaptureButton;
    public   TextView out;
    List<String> d=new ArrayList<>();
    ImageView imageView;
    ImageButton utility;
    ArrayList<CardContent> response=new ArrayList<>();
    long time;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    static final int MY_PERMISSION_REQUEST_FINE_LOCATION=123;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CardContent> cardContent=new ArrayList<>();
    ProgressDialog mProgressDialog;

    String call;//string which is used for making httprequest
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);


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
      startCaptureButton = (ImageButton)findViewById(R.id.kcapturebutton);
      utility=(ImageButton)findViewById(R.id.utility);
     //recycler view to generate cards
      mRecyclerView = (RecyclerView) findViewById(R.id.cardrecycle);

      // use a linear layout manager
      mLayoutManager = new LinearLayoutManager(this);
      mRecyclerView.setLayoutManager(mLayoutManager);

      mProgressDialog = new ProgressDialog(MainActivity.this);
      mProgressDialog.setMessage("Please wait! Fetching Data from Eventshop server...");
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      mProgressDialog.setCancelable(false);

      //end recycler
      time= System.currentTimeMillis();//get current system time
      android.util.Log.i("Time Class ", " Time value in millisecinds " + time);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
          buildAlertMessageNoGps();
      }
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
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


    public void startUtility(View view)
    {
        Intent intent= new Intent(this, FourSquareActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void startCameraView(View view)
    {
        Intent intent= new Intent(this, StartCameraActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void startWiki(View view)
    {
        Intent intent= new Intent(this, FourSquareSearchActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void startSearch(View view)
    {
        Intent intent= new Intent(this, SearchableActivity.class);
        MainActivity.this.startActivity(intent);
    }
    @Override
    public void onLocationChanged(Location location) {
        /*txtLat = (TextView) findViewById(R.id.locationText);
        txt = (TextView) findViewById(R.id.nameLocation);
        */
        //txtLat.setText("Lat:" + location.getLatitude() + "\nLong:" + location.getLongitude());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        double lat, lon;
        lat = location.getLatitude();
        lon = location.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            String street = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            //txt.setText(street + "  |  " + city + "  |  " + countryName);
            Log.i("Latitude: ", lat + ", Longitude: " + lon);

            //time=86400000;
				call="http://sln.ics.uci.edu:8085/eventshoplinux/rest/sttwebservice/search/71/circle/"+lat+","+lon+",0.01/"+"1457833742522"+"/null";
				System.out.println(call);
                new RequestTask().execute(call);  // getting response from async task


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

    @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
      if(id==R.id.action_refresh){
          try {
              new RequestTask().execute(call).get();
          } catch (InterruptedException e) {
              e.printStackTrace();
          } catch (ExecutionException e) {
              e.printStackTrace();
          }
          return true;
      }
    return super.onOptionsItemSelected(item);
  }
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
        String data="";Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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
                }});
            //pbar.setVisibility(View.INVISIBLE);
            mAdapter = new CardAdapter(result);

            mRecyclerView.setAdapter(mAdapter);
            //Log.i("MyResponse",result); //testing
 
        }


    }

}