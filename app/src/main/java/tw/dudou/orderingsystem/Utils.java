package tw.dudou.orderingsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
        String text = "";
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
    public static byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }
    public static Uri getOutputUri(){
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (fileDir.exists() == false)
            fileDir.mkdir();
        File file = new File(fileDir,"photo.png");
        Log.d("dudoudebug", file.getPath());
        return Uri.fromFile(file);
    }

    public static byte[] uriToBytes(Context context, Uri uri){
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            return baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String fetchFromURL(String URLString){
        URL urlObject = null;
        try {
            urlObject = new URL(URLString);
            URLConnection urlConnection = urlObject.openConnection();
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while( (len = is.read(buffer)) != -1 ) {
                baos.write(buffer, 0, len);
            }

            return new String(baos.toByteArray());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
