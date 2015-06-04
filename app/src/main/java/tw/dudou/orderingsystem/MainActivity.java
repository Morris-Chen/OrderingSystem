package tw.dudou.orderingsystem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private EditText inputEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = (EditText) findViewById(R.id.editText);
        inputEditText.setOnKeyListener(new View.OnKeyListener() {
            // onKeyListener's reference keyCode is reference in KeyEvent.KEYCODE_ENTER
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
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
    }

    void sendTextToToast(){

        String text = inputEditText.getText().toString();
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();

    }

    public void send2(View view){
        // if declare to xml: onClick, the function if and on if follows:
        // 1. it is a public function
        // 2. it has only one reference: View
        inputEditText.setText("");
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
