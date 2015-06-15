package tw.dudou.orderingsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final int MENU_ORDER_ACTIVITY = 1;
    private JSONObject menuInfo;

    private EditText inputEditText;
    private Button sendButton;
    private CheckBox toUpperCheckBox;
    private ListView historyListView;

    // to save all the data to /data/data/tw.dudou.ordersystem/shared_prefs/setting.xml
    private SharedPreferences saveState;
    private SharedPreferences.Editor saveStateEditor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "l6vG5tGkW0lZxWYvM7KIoW2lQNY8Ui2oaPhRtYMh",
                "9LGsSlN6BO9dnpA740y5oX1OCP0iLeD0dPVwEfF6");


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
                        sendTextToToast();
                        return true; // if you don't want to continue the key press to continue processing, return true.
                    }
                }
                return false; //if you still want Enter to show, return false and let the key press continue to process.
            }
        });


        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextToToast();
            }
        });

        historyListView = (ListView) findViewById(R.id.logView);

        toUpperCheckBox = (CheckBox) findViewById(R.id.checkBox);
        toUpperCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveStateEditor.putBoolean("checkBox",isChecked);
                saveStateEditor.commit();
            }
        });

        saveState = getSharedPreferences("Settings", Context.MODE_PRIVATE); //this notebook is private and cannot be accessed by others
        saveStateEditor = saveState.edit();

        inputEditText.setText(saveState.getString("EditText", "").toString());
        toUpperCheckBox.setChecked(saveState.getBoolean("checkBox",false));

        setHistoryData();

    }

    private void setHistoryData(){

        String buf = Utils.readFile(this, "LogHistory.txt");
        String[] entries = buf.split("\n");

        List<Map<String,String>> mapData = new ArrayList<>();

        for (int i = 0;i<entries.length;i++){

            Map <String,String> item = new HashMap<>();
            try {
                JSONObject order = new JSONObject(entries[i]);
                String note = order.getString("note");

                item.put("note",note);

                if (order.has("menu")) {
                    JSONArray menu = order.getJSONArray("menu");
                    item.put("orderItem", Utils.parseItemName(this,menu.toString()));
                    item.put("orderNum", "" + Utils.parseItemTotalNum(this,menu.toString()));
                }
                mapData.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        String[] from = {"note", "orderItem", "orderNum"};
        int[] to = {R.id.note,R.id.drinkName,R.id.drinkNum};
        //ArrayAdapter<String> historyLog = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entries);
        SimpleAdapter adapter_Log = new SimpleAdapter(this, mapData, R.layout.order_list_item,from, to);
        historyListView.setAdapter(adapter_Log);

    }

    private void sendTextToToast(){

        String text = inputEditText.getText().toString();
        JSONObject order = new JSONObject();


        text = toUpperCheckBox.isChecked()?text.replaceAll(".","*"):text;

        try{
            order.put("note", text);
            if (menuInfo !=null){
                order.put("menu",menuInfo.getJSONArray("Result"));
            }

            //use Parse.com to save entities
            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("note", order.getString("note"));
            orderObject.put("menu", order.getJSONArray("menu"));
            orderObject.saveInBackground();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.writeFile(this,"LogHistory.txt",order.toString()+ "\n");

        setHistoryData();
    }

    public void send2(View view){
        // if declare to xml: onClick, the function if and on if follows:
        // 1. it is a public function
        // 2. it has only one reference: View
        inputEditText.setText("");
        saveStateEditor.remove("EditText");
        saveStateEditor.commit();
    }

    public void goToMenu(View view){

        Intent intent = new Intent();
        intent.setClass(this,MenuActivity.class);
        startActivityForResult(intent, MENU_ORDER_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("dudoudebug", "" + requestCode);
        Log.d("dudoudebug", "" + resultCode);
        if(requestCode == MENU_ORDER_ACTIVITY && resultCode == RESULT_OK) {
            Log.d("dudoudebug", data.getStringExtra("orderList"));
            try {
                menuInfo = new JSONObject(data.getStringExtra("orderList"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /* TODO , find a better way to show the menu already selected
            TextView ordertemp = (TextView) findViewById(R.id.textView5);
            ordertemp.setText(Utils.parseItem(this,menuInfo.toString()));
            */
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
