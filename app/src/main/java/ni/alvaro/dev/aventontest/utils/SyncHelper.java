package ni.alvaro.dev.aventontest.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import ni.alvaro.dev.aventontest.database.BuddyRepo;
import ni.alvaro.dev.aventontest.models.Buddy;
import ni.alvaro.dev.aventontest.networking.RetrofitHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncHelper {
    private static final SyncHelper ourInstance = new SyncHelper();
    private static final String TAG = SyncHelper.class.getSimpleName();

    public static SyncHelper getInstance() {
        return ourInstance;
    }

    private SyncHelper() {
    }

    public void getBuddiesFromServer(Application application){
        RetrofitHelper.getInstance().getUserService().getMarkers().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse s = response.body();
                    if (s != null) {
                        if (s.getError() == 0) {
                            List<Buddy> markers = s.getResult();
                            AsyncTask.execute(() -> {
                                BuddyRepo r = new BuddyRepo(application);
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
