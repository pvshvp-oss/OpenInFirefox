package com.pattanshetti.shivanand.openinfirefox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    // https://stackoverflow.com/questions/3976616/how-to-find-nth-occurrence-of-character-in-a-string
    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        Context context = getApplicationContext();
        String action = intent.getAction();
        String type = intent.getType();
        try {
            Log.d("CUSTOM:Intent", intent.toString());
            Log.d("CUSTOM:Action", action.toString());
            Log.d("CUSTOM:Type", type.toString());
        } catch (Exception e) {
        }
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if ("text/html".equals(type)) {
                openInFirefox(context, intent);
            }
        }
    }

    void openInFirefox(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("CUSTOM:PathforURI", intent.getData().getPath());
        String URI = intent.getData().getPath();
        URI = URI.substring(ordinalIndexOf(URI, "/", 2) + 1);
        URI = "file://"/*+Environment.getExternalStorageDirectory().getPath()*/ + "/" + URI;
        Log.d("CUSTOM:URI", URI);
        Intent firefoxIntent = new Intent(intent.getAction());
        firefoxIntent.setDataAndType(Uri.parse(URI), intent.getType());
        // startActivity(Intent.createChooser(firefoxIntent, "Open file using:"));
        firefoxIntent.setPackage("org.mozilla.firefox");

        startActivity(firefoxIntent);
        this.finish();
    }
}
