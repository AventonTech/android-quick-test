package ni.alvaro.dev.aventontest.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ni.alvaro.dev.aventontest.R;
import ni.alvaro.dev.aventontest.models.Buddy;
import ni.alvaro.dev.aventontest.utils.SyncHelper;
import ni.alvaro.dev.aventontest.utils.Util;
import ni.alvaro.dev.aventontest.viewmodels.BuddyViewModel;
import ni.alvaro.dev.aventontest.views.fragments.PermissionsDeniedFragment;
import ni.alvaro.dev.aventontest.views.fragments.ProfileFragment;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;

public class MainActivity extends AppCompatActivity implements PermissionsDeniedFragment.OnFragmentInteractionListener, MapboxMap.OnMapClickListener, Observer<List<Buddy>> {
    private static final String  GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String MAP_TAG = "map_tag";

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
    private FeatureCollection usersFeatureCollection;
    private Style mStyle;
    private GeoJsonSource geoJsonSource;

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
                == PackageManager.PERMISSION_GRANTED) {
            loadMap();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            showSnackBar(R.string.updating_data_from_server);
            SyncHelper.getInstance().getBuddiesFromServer(getApplication());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(int stringRes) {
        Snackbar.make(findViewById(R.id.container), stringRes, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void findUserLocation(Style style) {
        // Get an instance of the component
        this.mStyle = style;
        LocationComponent locationComponent = mapBox.getLocationComponent();

        locationComponent.activateLocationComponent(MainActivity.this, style);

        locationComponent.setLocationComponentEnabled(true);

        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);

        BuddyViewModel mBuddyProviders = ViewModelProviders.of(this).get(BuddyViewModel.class);
        mBuddyProviders.getBuddies().observeForever(this);
    }

    private void loadMap() {
        mapsFragment.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            this.mapBox = mapboxMap;
            this.mapBox.addOnMapClickListener(this);
            findUserLocation(style);
        }));
    }

    private void setUpInfoWindowLayer(@NonNull Style loadedStyle) {
        loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage("{name}"),

                        PropertyFactory.iconAnchor(ICON_ANCHOR_BOTTOM),

                        PropertyFactory.iconAllowOverlap(true),

                        PropertyFactory.iconOffset(new Float[]{-2f, -25f})
                )
                .withFilter(Expression.eq((get(PROPERTY_SELECTED)), literal(true))));

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
        return handleClickIcon(mapBox.getProjection().toScreenLocation(point));
    }

    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapBox.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty(PROPERTY_NAME);
            if (usersFeatureCollection != null) {

                List<Feature> featureList = usersFeatureCollection.features();
                if (featureList != null) {
                    for (Feature f :
                            featureList) {
                        if (f.getStringProperty(PROPERTY_NAME).equals(name)) {
                            Log.i(TAG, "handleClickIcon: Feature selected -- " + f.toJson());

                            Snackbar.make(findViewById(R.id.container),
                                    getString(R.string.marker_selected, f.getStringProperty("name"))
                                    , Snackbar.LENGTH_SHORT).show();


                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onChanged(List<Buddy> buddies) {
        ArrayList<Feature> features = new ArrayList<>();

        for (Buddy m :
                buddies) {
            Feature f = Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(m.getLg()), Double.parseDouble(m.getLt())));
            f.addStringProperty("name", m.getName());
            if (geoJsonSource == null) {
                f.addBooleanProperty("selected", false);
            }
            features.add(f);
        }
        usersFeatureCollection = FeatureCollection.fromFeatures(features);
        if (geoJsonSource != null) {
            refreshGeoSource();
        } else {

            geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE_ID, usersFeatureCollection);
            SymbolLayer users = new SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID);

            users.setProperties(
                    PropertyFactory.iconImage(MARKER_IMAGE_ID)

            );

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            mStyle.addImage(MARKER_IMAGE_ID, icon);
            mStyle.addSource(geoJsonSource);
            mStyle.addLayer(users);

            setUpInfoWindowLayer(mStyle);

        }


        showSnackBar(R.string.data_updated);
    }

    private void refreshGeoSource() {
        if (geoJsonSource != null && usersFeatureCollection != null) {
            geoJsonSource.setGeoJson(usersFeatureCollection);
        }
    }


}
