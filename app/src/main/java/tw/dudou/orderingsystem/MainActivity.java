package tw.dudou.orderingsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

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

        String buf = Utils.readFile(this,"LogHistory.txt");
        String[] entries = buf.split("\n");

        ArrayAdapter<String> histroyLog =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entries);
        historyListView.setAdapter(histroyLog);

    }

    private void sendTextToToast(){

        String text = inputEditText.getText().toString();
        //Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,toUpperCheckBox.isChecked()?text.replaceAll(".","*"):text,Toast.LENGTH_SHORT).show();
        //((TextView) findViewById(R.id.textView)).setText(toUpperCheckBox.isChecked() ? text.toUpperCase() : text);
        Utils.writeFile(this,"LogHistory.txt",text + "\n");
        //text = Utils.readFile(this,"LogHistory.txt");
        //Toast.makeText(this,toUpperCheckBox.isChecked()?text.replaceAll(".","*"):text,Toast.LENGTH_SHORT).show();
        //((TextView) findViewById(R.id.logView)).setText(text);
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
        startActivity(intent);

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
