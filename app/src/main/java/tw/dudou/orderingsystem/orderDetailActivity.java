package tw.dudou.orderingsystem;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class orderDetailActivity extends ActionBarActivity {


    private WebView mapView;
    private TextView addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        addressText = (TextView) findViewById(R.id.address);

        String address = getIntent().getStringExtra("address");
        addressText.setText(address);

        mapView = (WebView) findViewById(R.id.staticMap);

        asyncTask.execute(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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
                        "&size=600x600&markers=color:blue%%7Clabel:S%%7C%f,%f",
                        lat,lng,lat,lng);

                mapView.loadUrl(mapURL);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}

//https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&markers=color:blue%7Clabel:S%7C40.702147,-74.015794
