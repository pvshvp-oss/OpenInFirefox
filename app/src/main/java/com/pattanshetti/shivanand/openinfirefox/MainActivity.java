package com.pattanshetti.shivanand.openinfirefox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.app.*;
import android.content.DialogInterface;
import android.os.*;

import java.lang.reflect.Method;

import static android.widget.Toast.LENGTH_LONG;

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
        setTitle("Open In Firefox");

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

    void openInFirefox(final Context context, Intent intent) {
        try {
            // https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Intent firefoxIntent = new Intent(intent.getAction());
            PathDecoder pathDecoder = new PathDecoder();
            Log.d("CUSTOM:PathforURI", intent.getData().getPath());
            firefoxIntent.setDataAndType(Uri.parse(pathDecoder.getPathFromURI(intent.getData())), intent.getType());
            Log.d("CUSTOM:ParsedPath", pathDecoder.getPathFromURI(intent.getData()));
            // startActivity(Intent.createChooser(firefoxIntent, "Open file using:"));
            firefoxIntent.setPackage("org.mozilla.firefox");
            startActivity(firefoxIntent);
            this.finish();
        } catch (Exception e) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getApplicationContext());
            }
            builder.setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }
}
