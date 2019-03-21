package ni.alvaro.dev.aventontest.views;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ni.alvaro.dev.aventontest.R;
import ni.alvaro.dev.aventontest.utils.Util;
import ni.alvaro.dev.aventontest.views.fragments.MapFragment;
import ni.alvaro.dev.aventontest.views.fragments.ProfileFragment;

import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    private static final int MAP_LOCATION_REQUEST_CODE = 7777;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.maps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, MapFragment.newInstance()).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, ProfileFragment.newInstance()).commit();
                        return true;
                }
                return false;
            };
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AsyncTask.execute(() -> Util.createAlertBuilder(MainActivity.this,R.string.location_request_rationale)
                        .setCancelable(false)
                        .setPositiveButton(R.string.grant, (dialog, which) -> {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
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
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MAP_LOCATION_REQUEST_CODE);


            }
        }

    }


}
