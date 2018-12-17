package com.pattanshetti.shivanand.openinfirefox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.os.Environment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        try {
            Log.d("CUSTOM:Intent", intent.toString());
            Log.d("CUSTOM:Action", action.toString());
            Log.d("CUSTOM:Type", type.toString());
        }catch(Exception e){
        }
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if ("text/html".equals(type)) {
                openInFirefox(intent);
            }
        }
    }

    void openInFirefox(Intent intent){
        String URI = intent.getData().getPath().replace("/external_files/", "file://"+Environment.getExternalStorageDirectory().getPath()+"/");
    }
}
