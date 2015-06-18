package tw.dudou.orderingsystem;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void resetOrder(View view){
        LinearLayout root = (LinearLayout) findViewById(R.id.root);
        int count = root.getChildCount();

        for (int i = 0; i < count -1; i++ ){
            LinearLayout item = (LinearLayout) root.getChildAt(i);
            int item_layout = item.getChildCount();
            for (int j=1;j<item_layout;j++ ) {
                ((TextView)item.getChildAt(j)).setText("0");
            }
        }
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
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();

        for (int i = 0; i < count -1; i++ ){

            JSONObject itemStatus = new JSONObject();

            LinearLayout item = (LinearLayout) root.getChildAt(i);
            int item_layout = item.getChildCount();

            String item_name = ((TextView)item.getChildAt(0)).getText().toString();

            int[] item_type =new int[3]; // HARD CODE there will be no more than 4 types
            boolean isEmpty = true;

            for (int j=1;j<item_layout;j++ ) {
                item_type[j-1]=Integer.parseInt(((TextView) item.getChildAt(j)).getText().toString());
                if(isEmpty) isEmpty = item_type[j-1] == 0;
            }

            if (isEmpty) {
                continue;
            }

            try {
                itemStatus.put("itemName",item_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j=0;j<3;j++) {
                try {
                    if(item_type[j] !=0) {
                        itemStatus.put("Type" + j, item_type[j]);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            items.put(itemStatus);
        }
        try {
            result.put("Result", items);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    public void sendOrder(View view){
        // declare a intent to go back, set data, set result code
        Intent intent = new Intent();
        intent.putExtra("orderList" , getSummary());


        setResult(RESULT_OK,intent);
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
