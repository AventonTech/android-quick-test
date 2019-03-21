package ni.alvaro.dev.aventontest;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import ni.alvaro.dev.aventontest.database.BuddyRepo;
import ni.alvaro.dev.aventontest.models.Buddy;
import ni.alvaro.dev.aventontest.networking.RetrofitHelper;
import ni.alvaro.dev.aventontest.utils.PropertyManager;
import ni.alvaro.dev.aventontest.utils.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AventonApp extends Application {

    private static final String TAG = AventonApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: App created!");
        try {
            PropertyManager.initialize(this);
            Mapbox.getInstance(this,PropertyManager.getInstance().getSecureMapKey());

            fetchBuddies();
        } catch (IOException e) {
            Log.i(TAG, "onCreate: App couln't load properties file!");
        }

    }

    private void fetchBuddies() {
        RetrofitHelper.getInstance().getUserService().getMarkers().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse s = response.body();
                    if (s != null) {
                        if (s.getError() == 0) {
                            List<Buddy> markers = s.getResult();
                            AsyncTask.execute(() -> {
                                BuddyRepo r = new BuddyRepo(AventonApp.this);
                                r.insertAll(markers.toArray(new Buddy[0]));
                            });
                        } else {
                            Log.e(TAG, "onResponse: Server responded with error code::" + s.getErrorCode() + " --> " + s.getMsg());
                        }
                    }
                } else {
                    Log.e(TAG, "onResponse: No data from server retrieved");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.i(TAG, "onFailure: No data from server retrieved!");
            }
        });
    }
}
