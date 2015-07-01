package tw.dudou.orderingsystem;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by dudou on 2015/6/22.
 */
public class OrderingSystemApplication extends Application{
    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "l6vG5tGkW0lZxWYvM7KIoW2lQNY8Ui2oaPhRtYMh",
                "9LGsSlN6BO9dnpA740y5oX1OCP0iLeD0dPVwEfF6");
        //Parse.initialize(this, "PihJMpOOpNYxpXN8wYcd3Jvn6R1x6IHOl6TA5gKc", "mnPmwNUDinSNH3b4RRiScFdkNRgLFxK61DVIpXYI");
        super.onCreate();
    }
}
