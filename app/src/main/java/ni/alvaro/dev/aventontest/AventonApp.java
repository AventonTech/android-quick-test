package ni.alvaro.dev.aventontest;

import android.app.Application;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;

import java.io.IOException;

import ni.alvaro.dev.aventontest.utils.PropertyManager;
import ni.alvaro.dev.aventontest.utils.SyncHelper;

public class AventonApp extends Application {

    private static final String TAG = AventonApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: App created!");
        try {
            PropertyManager.initialize(this);
            Mapbox.getInstance(this,PropertyManager.getInstance().getSecureMapKey());
            SyncHelper.getInstance().getBuddiesFromServer(this);
        } catch (IOException e) {
            Log.i(TAG, "onCreate: App couln't load properties file!");
        }

    }

}
