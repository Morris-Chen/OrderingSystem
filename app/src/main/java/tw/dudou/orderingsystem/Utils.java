package tw.dudou.orderingsystem;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dudou on 2015/6/8.
 */
public class Utils {

    public static void writeFile(Context context,String fileName, String fileContent){
        // Will automatically create file on the last file access place. in this project, it will wirte to:
        // data/data/com.example.simple
        try {
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_APPEND); // openFileOutput will through excetption
            fout.write(fileContent.getBytes()); //fout will through exception
            fout.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static String readFile(Context context, String fileName){
        // THIS IS NOT A NICE WAY TO READ FILE
        // should be revisited in next few lessons.
        try{
            FileInputStream fin = context.openFileInput(fileName);
            byte[] buf = new byte[1024];
            fin.read(buf);
            fin.close();
            return new String(buf);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String parseItemSummary(Context context, String jsonText){
        String text = "Menu: \n";
        try {
            JSONObject input = new JSONObject(jsonText);
            JSONArray menuJSON = input.getJSONArray("Result");
            for (int i =0;i<menuJSON.length();i++){
                JSONObject menuItem = menuJSON.getJSONObject(i);
                text+= menuItem.getString("itemName") + ": ";
                if(menuItem.has("Type0")) text += "S["+menuItem.getInt("Type0")+"] ";
                if(menuItem.has("Type1")) text += "M["+menuItem.getInt("Type1")+"] ";
                if(menuItem.has("Type2")) text += "L["+menuItem.getInt("Type2")+"] ";
                text.trim();
                text +="\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return text;
    }
    public static int parseItemTotalNum(Context context, String jsonText){
        int ret = 0;
        try {
            JSONArray menuJSON = new JSONArray(jsonText);
            for (int i=0;i<menuJSON.length();i++){
                JSONObject menuItem = menuJSON.getJSONObject(i);
                if(menuItem.has("Type0")) ret += menuItem.getInt("Type0");
                if(menuItem.has("Type1")) ret += menuItem.getInt("Type1");
                if(menuItem.has("Type2")) ret += menuItem.getInt("Type2");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
    public static String parseItemName(Context context, String jsonText){
        String text = "";

        try {
            JSONArray menuJSON = new JSONArray(jsonText);
            for (int i=0;i<menuJSON.length();i++){
                JSONObject menuItem = menuJSON.getJSONObject(i);
                String Name = menuItem.getString("itemName");
                text += Name;
                text += ", ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return text.substring(0,text.lastIndexOf(", "));
    }
}
