package ni.alvaro.dev.aventontest.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ni.alvaro.dev.aventontest.R;
import ni.alvaro.dev.aventontest.networking.RetrofitHelper;
import ni.alvaro.dev.aventontest.utils.MapMarker;
import ni.alvaro.dev.aventontest.utils.ServerResponse;
import ni.alvaro.dev.aventontest.utils.Util;
import ni.alvaro.dev.aventontest.views.fragments.PermissionsDeniedFragment;
import ni.alvaro.dev.aventontest.views.fragments.ProfileFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PermissionsDeniedFragment.OnFragmentInteractionListener,MapboxMap.OnMapClickListener{

    private static final String MAP_TAG = "map_tag";
    private static final String MARKERS = "MARKERS";
    MapboxMapOptions options = new MapboxMapOptions().camera(new CameraPosition.Builder()
            .zoom(12)
            .build());
    final SupportMapFragment mapsFragment = SupportMapFragment.newInstance(options);
    final Fragment profileFragment = ProfileFragment.newInstance();
    final Fragment permissionDeniedFragment = PermissionsDeniedFragment.newInstance(R.string.location_request_rationale);
    private Fragment currentFrag = mapsFragment;
    final FragmentManager fm = getSupportFragmentManager();

    private static final int MAP_LOCATION_REQUEST_CODE = 7777;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.profile:
                fm.beginTransaction().hide(currentFrag)
                        .show(profileFragment)
                        .commit();
                currentFrag = profileFragment;
                return true;
            case R.id.maps:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fm.beginTransaction().hide(currentFrag).show(mapsFragment)
                            .commit();
                    currentFrag = mapsFragment;
                } else {
                    fm.beginTransaction()
                            .hide(currentFrag)
                            .show(permissionDeniedFragment).commit();
                    currentFrag = permissionDeniedFragment;
                }

                return true;
        }

        return false;
    };
    private String TAG = MainActivity.class.getSimpleName();
    private MapboxMap mapBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.main_content, mapsFragment, MAP_TAG).commit();
        fm.beginTransaction().add(R.id.main_content, profileFragment, ProfileFragment.TAG).hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_content, permissionDeniedFragment, PermissionsDeniedFragment.TAG).hide(permissionDeniedFragment).commit();
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.maps);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            loadMap();

        }
    }


    @SuppressLint("MissingPermission")
    private void findUserLocation(Style style) {
        // Get an instance of the component
        LocationComponent locationComponent = mapBox.getLocationComponent();

        locationComponent.activateLocationComponent(MainActivity.this, style);

        locationComponent.setLocationComponentEnabled(true);

        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
        RetrofitHelper.getInstance().getUserService().getMarkers().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()){
                    ServerResponse s = response.body();
                    if (s != null) {
                        Gson g = new Gson();
                        if (s.getError() == 0){
                            List<MapMarker> markers = s.getResult();
                            ArrayList<Feature> features = new ArrayList<>();

                            for (MapMarker m :
                                    markers) {
                                Feature f = Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(m.getLg()), Double.parseDouble(m.getLt())));
                                f.addStringProperty("name",m.getName());
                                features.add(f);
                            }

                            FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);


                            GeoJsonSource geoJsonSource = new GeoJsonSource("users-geojson-source", featureCollection);
                            style.addSource(geoJsonSource);


                        }else{
                            Log.e(TAG, "onResponse: Server responded with error code::"+s.getErrorCode()+" --> "+s.getMsg());
                        }
                    }
                }else{
                    Log.e(TAG, "onResponse: No data from server retrieved");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Log.i(TAG, "onFailure: No data from server retrieved!");
            }
        });

    }

    private void loadMap() {
        mapsFragment.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            this.mapBox = mapboxMap;
            findUserLocation(style);
        }));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MAP_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fm.beginTransaction().hide(currentFrag).show(mapsFragment)
                            .commit();
                    currentFrag = mapsFragment;

                   loadMap();

                }
            }
        }
    }

    @Override
    public void onFragmentInteraction() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AsyncTask.execute(() -> Util.createAlertBuilder(MainActivity.this, R.string.location_request_rationale)
                        .setCancelable(false)
                        .setPositiveButton(R.string.grant, (dialog, which) -> {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MAP_LOCATION_REQUEST_CODE);
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.deny, (dialog, which) -> {
                            Log.e(TAG, "onClick: User denied location access");
                            dialog.dismiss();
                        })
                        .create().show());


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MAP_LOCATION_REQUEST_CODE);


            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapsFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapsFragment.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapsFragment.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapsFragment.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapsFragment.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapsFragment.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }
}
