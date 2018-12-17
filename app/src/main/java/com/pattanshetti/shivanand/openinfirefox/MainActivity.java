package com.pattanshetti.shivanand.openinfirefox;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.Intent;
import android.util.Log;
import android.os.Environment;
import java.lang.reflect.*;

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
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String URI = intent.getData().getPath().replace("/external_files/", "file://"+Environment.getExternalStorageDirectory().getPath()+"/");
        Intent firefoxIntent = new Intent(intent.getAction());
        firefoxIntent.setDataAndType(Uri.parse(URI), intent.getType());
        // startActivity(Intent.createChooser(firefoxIntent, "Open file using:"));
        firefoxIntent.setPackage("org.mozilla.firefox");
        startActivity(firefoxIntent);
        this.finish();
    }
}
