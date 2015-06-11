package tw.dudou.orderingsystem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }


    public void pick(View view){

        Button button = (Button) view;
        String stringInButton = button.getText().toString();
        int count = Integer.parseInt(stringInButton) + 1;

        button.setText(String.valueOf(count));
    }

    public String getSummary(){

        LinearLayout root = (LinearLayout) findViewById(R.id.root);
        int count = root.getChildCount();
        String all ="";
        for (int i = 0; i < count -1; i++ ){
            LinearLayout item = (LinearLayout) root.getChildAt(i);
            int item_layout = item.getChildCount();
            String item_name = ((TextView)item.getChildAt(0)).getText().toString();
            all += item_name +",";
            int[] item_count =new int[3];
            for (int j=1;j<item_layout;j++ ) {
                item_count[j-1]=Integer.parseInt(((TextView) item.getChildAt(j)).getText().toString());

            }
            for (int j=0;j<3;j++) {
                all += String.valueOf(item_count[j]) + ",";
            }
            all += "\n";
        }
        return all;
    }
    public void sendOrder(View view){
        Toast.makeText(this,getSummary(),Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
