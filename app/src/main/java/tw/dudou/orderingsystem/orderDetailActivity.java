package tw.dudou.orderingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class orderDetailActivity extends AppCompatActivity {


    private WebView mapView;
    private TextView addressText;
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private GoogleMap mMap;
    private boolean imageMapVisible, webviewMapVisible;
    private boolean imageMapLoaded, webviewMapLoaded;
    private int imageMapH,webviewMapH;
    private String storeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        addressText = (TextView) findViewById(R.id.address);

        storeAddress = getIntent().getStringExtra("address");
        addressText.setText(storeAddress);

        mapView = (WebView) findViewById(R.id.staticMap);
        imageView = (ImageView) findViewById(R.id.imageMapView);
        progressDialog = new ProgressDialog(this);

        mapView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mymap))
                    .getMap();
            // Check if we were successful in obtaining the map.
        }
        imageMapVisible = false;
        imageMapLoaded = false;
        webviewMapVisible = false;
        webviewMapLoaded = false;


    }

    public void showImageMap(View view){
        if(imageMapVisible){
            imageView.setVisibility(View.GONE);
            imageMapVisible = false;
            ((Button)findViewById(R.id.ImageViewButton)).setText("Show ImageView");
        } else {
            imageView.setVisibility(View.VISIBLE);
            if(!imageMapLoaded) {
                ImageLoader img = new ImageLoader();
                img.execute(storeAddress);
            }
            imageMapVisible = true;
            ((Button)findViewById(R.id.ImageViewButton)).setText("Hide ImageView");
            imageMapLoaded = true;
        }

    }

    public void showWebViewMap(View view){
        if(webviewMapVisible){
            mapView.setVisibility(View.GONE);
            ((Button)findViewById(R.id.WebvViewButton)).setText("Show WebView");
            webviewMapVisible = false;

        }
        else{
            mapView.setVisibility(View.VISIBLE);
            if(!webviewMapLoaded) asyncTask.execute(storeAddress);
            ((Button)findViewById(R.id.WebvViewButton)).setText("Hide WebView");
            webviewMapVisible = true;
            webviewMapLoaded = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    public void gotoGoogleMap(View view){
        String address = ((TextView)findViewById(R.id.address)).getText().toString();
        Intent intent = new Intent();
        intent.setClass(this,MapsActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("addJSON", address);
        startActivity(intent);
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

        return super.onOptionsItemSelected(item);
    }

    AsyncTask <String,Void,String> asyncTask = new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String out ="";
            try {
                out = Utils.fetchFromURL(
                        "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + URLEncoder.encode(address,"utf-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("debug",out);
            return out;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            JSONObject object = null;
            try {
                object = new JSONObject(jsonString);
                JSONObject position = object.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double lat = position.getDouble("lat");
                double lng = position.getDouble("lng");

                String mapURL = String.format(
                        "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13" +
                        "&size=400x400&markers=color:blue%%7Clabel:S%%7C%f,%f",
                        lat,lng,lat,lng);

                mapView.loadUrl(mapURL);

                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                            .title("Store")
                            .snippet(addressText.getText().toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),13));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    class ImageLoader extends AsyncTask<String, Void, byte[]> {
        @Override
        protected void onPreExecute() {
        }


        @Override
        protected byte[] doInBackground(String... params) {
            try {

                String address = params[0];
                String out ="";

                try {
                    out = Utils.fetchFromURL(
                            "https://maps.googleapis.com/maps/api/geocode/json?address="
                                    + URLEncoder.encode(address,"utf-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("debug", out);

                JSONObject object = new JSONObject(out);
                JSONObject position = object.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double lat = position.getDouble("lat");
                double lng = position.getDouble("lng");

                String mapURL = String.format(
                        "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13" +
                                "&size=400x400&markers=color:blue%%7Clabel:S%%7C%f,%f",
                        lat, lng, lat, lng);


                URL urlObject = new URL(mapURL);
                URLConnection urlConnection = urlObject.openConnection();
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while( (len = is.read(buffer)) != -1 ) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new byte[0];
        }

        @Override
        protected void onPostExecute(byte[] bytes) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imageView.setImageBitmap(bitmap);
            imageMapLoaded = true;
        }
    }
}

//https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&markers=color:blue%7Clabel:S%7C40.702147,-74.015794
