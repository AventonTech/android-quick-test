package ni.alvaro.dev.aventontest.networking.services;

import ni.alvaro.dev.aventontest.utils.ServerResponse;
import retrofit2.Call;
import retrofit2.http.POST;

public interface MapMarkerService {

    @POST("api/test")
    Call<ServerResponse> getMarkers();
}
