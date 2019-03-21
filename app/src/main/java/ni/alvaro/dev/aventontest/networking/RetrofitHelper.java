package ni.alvaro.dev.aventontest.networking;

import java.util.concurrent.TimeUnit;

import ni.alvaro.dev.aventontest.BuildConfig;
import ni.alvaro.dev.aventontest.networking.services.MapMarkerService;
import ni.alvaro.dev.aventontest.utils.PropertyManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static RetrofitHelper instance;

    static {
        instance = null;
    }

    private MapMarkerService mapMarkerService;

    private RetrofitHelper() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient client = okHttpBuilder
                .connectTimeout(100, TimeUnit.SECONDS)
                .pingInterval(2,TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyManager.getInstance().getApiURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        mapMarkerService = retrofit.create(MapMarkerService.class);


    }

    public static RetrofitHelper getInstance() {
        if (instance == null) {
            instance = new RetrofitHelper();
        }

        return instance;
    }

    public MapMarkerService getUserService() {
        return mapMarkerService;
    }

}
