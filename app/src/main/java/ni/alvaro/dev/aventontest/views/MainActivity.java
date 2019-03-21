package ni.alvaro.dev.aventontest.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ni.alvaro.dev.aventontest.R;
import ni.alvaro.dev.aventontest.utils.Util;
import ni.alvaro.dev.aventontest.views.fragments.MapFragment;
import ni.alvaro.dev.aventontest.views.fragments.PermissionsDeniedFragment;
import ni.alvaro.dev.aventontest.views.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements PermissionsDeniedFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener {

    final Fragment mapsFragment = MapFragment.newInstance();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.main_content, mapsFragment, MapFragment.TAG).commit();
        fm.beginTransaction().add(R.id.main_content, profileFragment, ProfileFragment.TAG).hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_content, permissionDeniedFragment, PermissionsDeniedFragment.TAG).hide(permissionDeniedFragment).commit();
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.maps);
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
    public void onPersonMarkerInteraction(Person person) {
        Log.i(TAG, "onPersonMarkerInteraction: Person clicked " + person.getName());
    }
}
