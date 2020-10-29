package com.tandg.tagscanner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Handler handler;
    private static final int STORAGE_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {



                checkPermission(
                        Manifest.permission.NFC,
                        STORAGE_PERMISSION_CODE);




            }
        },1000);
    }


    private void checkPermission(String nfc, int storagePermissionCode) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, nfc)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { nfc},
                    storagePermissionCode);
        }
        else {

            navigateToMainScreen();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                navigateToMainScreen();

            }
            else {
                Log.e(TAG, "onRequestPermissionsResult: Denied " );
            }
        }
    }
    private void navigateToMainScreen() {

        //Add main activity name

        Intent intent = new Intent(MainActivity.this, nfcActivity.class);
        startActivity(intent);
        finish();

    }
}
