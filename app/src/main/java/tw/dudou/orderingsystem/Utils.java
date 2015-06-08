package tw.dudou.orderingsystem;

import android.content.Context;

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
}
