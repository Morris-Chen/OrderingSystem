package tw.dudou.orderingsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //******* Constant definition
    private static final int MENU_ORDER_ACTIVITY = 1;
    private static final int TAKE_PHOTO_ACTIVITY = 2;

    //******* UI declarition
    private EditText inputEditText;
    private Button sendButton;
    private CheckBox toUpperCheckBox;
    private ListView historyListView;
    private Spinner spinner;

    //******* Preference
    // to save all the data to /data/data/tw.dudou.ordersystem/shared_prefs/setting.xml
    private SharedPreferences saveState;
    private SharedPreferences.Editor saveStateEditor;
    private boolean hasPhoto = false;
    private Bitmap bitmap;

    //******* Temp Stack variable
    private JSONObject menuInfo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*

         */
        inputEditText = (EditText) findViewById(R.id.editText);
        inputEditText.setOnKeyListener(new View.OnKeyListener() {
            // onKeyListener's reference keyCode is reference in KeyEvent.KEYCODE_ENTER
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // first to add to save state
                saveStateEditor.putString("EditText", inputEditText.getText().toString());
                saveStateEditor.commit();

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && !inputEditText.getText().toString().isEmpty()) {
                        sendOrder();
                        return true; // if you don't want to continue the key press to continue processing, return true.
                    }
                }
                return false; //if you still want Enter to show, return false and let the key press continue to process.
            }
        });


        spinner = (Spinner) findViewById(R.id.spinner);

        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder();
            }
        });

        historyListView = (ListView) findViewById(R.id.logView);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = ((TextView) findViewById(R.id.storeInfo)).getText().toString().split(",")[1];
                gotoOrderDetail(address);
            }
        });

        toUpperCheckBox = (CheckBox) findViewById(R.id.checkBox);
        toUpperCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ImageView) findViewById(R.id.imageView)).setVisibility(isChecked?View.INVISIBLE: View.VISIBLE);
                saveStateEditor.putBoolean("checkBox",isChecked);
                saveStateEditor.commit();
            }
        });

        saveState = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //this setting is private and cannot be accessed by others
        saveStateEditor = saveState.edit();

        inputEditText.setText(saveState.getString("EditText", "").toString());
        toUpperCheckBox.setChecked(saveState.getBoolean("checkBox", false));

        //Data initialization
        setStoreData();
        setHistoryData();

    }

    private void gotoOrderDetail(String address){
        /*


         */
        Intent intent = new Intent();
        intent.setClass(this,orderDetailActivity.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }

    private void setStoreData(){
        //String[] dataList = getResources().getStringArray(R.array.store_name);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<String> dataList = new ArrayList<String>();
                for (ParseObject object : list) {
                    dataList.add(object.getString("name") + "," + object.getString("address"));

                    ArrayAdapter adapter = new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_spinner_item, dataList);
                    spinner.setAdapter(adapter);
                }
            }
        });


    }
    private void setHistoryData(){

        final List<Map<String, String>> mapData = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    Map<String, String> item = new HashMap<>();

                    ParseObject order = list.get(i);

                    String note = order.getString("note");
                    JSONArray menuInfo = order.getJSONArray("menu");

                    item.put("note", note);
                    item.put("orderItem", Utils.parseItemName(MainActivity.this, menuInfo.toString()));
                    item.put("orderNum", "" + Utils.parseItemTotalNum(MainActivity.this, menuInfo.toString()));
                    item.put("storeInfo", order.getString("storeinfo"));

                    mapData.add(item);
                }


                String[] from = {"note", "orderItem", "orderNum", "storeInfo"};
                int[] to = {R.id.note, R.id.drinkName, R.id.drinkNum, R.id.storeInfo};
                //ArrayAdapter<String> historyLog = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entries);
                SimpleAdapter adapter_Log = new SimpleAdapter(MainActivity.this, mapData, R.layout.order_list_item, from, to);
                historyListView.setAdapter(adapter_Log);
            }
        });


    }

    private void sendOrder(){

        String text = inputEditText.getText().toString();
        JSONObject order = new JSONObject();
        if(menuInfo==null) {
            Toast.makeText(this,"You haven't select any drinks!",Toast.LENGTH_SHORT).show();
            return;
        }

        text = toUpperCheckBox.isChecked()?text.replaceAll(".","*"):text;
        String storeInfo = (String) spinner.getSelectedItem();

        try{
            order.put("note", text);
            if (menuInfo !=null){
                order.put("menu",menuInfo.getJSONArray("Result"));
            }

            //use Parse.com to save entities
            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("note", order.getString("note"));
            orderObject.put("menu", order.getJSONArray("menu"));
            orderObject.put("storeinfo", storeInfo);
            orderObject.pinInBackground();
            if (hasPhoto){
                ParseFile file = new ParseFile("photo.png",Utils.uriToBytes(this,Utils.getOutputUri()));
                orderObject.put("photo",file);
            }
            orderObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("debug", "done");
                    setHistoryData();
                    clearAll();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.writeFile(this, "LogHistory.txt", order.toString() + "\n");

    }

    private void clearAll(){
        inputEditText.setText("");
        saveStateEditor.remove("EditText");
        saveStateEditor.commit();
        menuInfo = null;
        bitmap = null;
        hasPhoto = false;
        ImageView photo = (ImageView) findViewById(R.id.imageView);
        TextView menu = (TextView) findViewById(R.id.textView5);
        menu.setText("Menu:\n(Empty)");
    }
    public void send2(View view){
        // if declare to xml: onClick, the function if and on if follows:
        // 1. it is a public function
        // 2. it has only one reference: View
        clearAll();
    }

    public void goToMenu(View view){

        String storeInfo = (String) spinner.getSelectedItem();

        Intent intent = new Intent();
        intent.putExtra("storeInfo", storeInfo);
        intent.setClass(this, MenuActivity.class);
        startActivityForResult(intent, MENU_ORDER_ACTIVITY);

    }

    public void gotoCamera(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Utils.getOutputUri());
        startActivityForResult(intent,TAKE_PHOTO_ACTIVITY);
    }

    private void showMenu(){

        TextView orderTemp = (TextView) findViewById(R.id.textView5);
        orderTemp.setText(menuInfo==null ? "Menu: " : "Menu:\n"+Utils.parseItemSummary(this, menuInfo.toString()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("dudoudebug", "main.onActivityResult.requestCode = " + requestCode);
        Log.d("dudoudebug", "main.onActivityResult.resultCode = " + resultCode);
        switch (requestCode){
            case MENU_ORDER_ACTIVITY:
                if(resultCode == RESULT_OK){
                    Log.d("dudoudebug", data.getStringExtra("orderList"));
                    try {
                        menuInfo = new JSONObject(data.getStringExtra("orderList"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO , find a better way to show the menu already selected (done).
                    showMenu();
                }
                break;
            case TAKE_PHOTO_ACTIVITY:
                if(resultCode == RESULT_OK){
                    hasPhoto = true;
                    //bitmap = data.getParcelableExtra("data");
                    ImageView photo = (ImageView) findViewById(R.id.imageView);
                    photo.setImageURI(Utils.getOutputUri());
                }
        }
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
        if (id == R.id.action_take_photo) {
            gotoCamera();
        }

        return super.onOptionsItemSelected(item);
    }
}
