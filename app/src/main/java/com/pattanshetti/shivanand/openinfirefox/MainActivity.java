package com.pattanshetti.shivanand.openinfirefox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.app.*;
import android.content.DialogInterface;
import android.os.*;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
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
        /*
        try {
            Log.d("CUSTOM:Intent", intent.toString());
            Log.d("CUSTOM:Action", action.toString());
            Log.d("CUSTOM:Type", type.toString());
        } catch (Exception e) {
        }
        */
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if ("text/html".equals(type)) {
                openInFirefox(context, intent);
            }
        }
    }

    void openInFirefox(final Context context, final Intent intent) {
        Intent firefoxIntent;
        PathDecoder pathDecoder;
        String fullUsablePath;
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

            firefoxIntent = new Intent(intent.getAction());
            pathDecoder = new PathDecoder();
            // Log.d("CUSTOM:PathforURI", intent.getData().getPath());
            fullUsablePath = pathDecoder.getPathFromURI(intent.getData());
            firefoxIntent.setDataAndType(Uri.parse(fullUsablePath), intent.getType());
            // Log.d("CUSTOM:ParsedPath", fullUsablePath);
            if (!new File(fullUsablePath.replaceFirst("file:///", "/")).exists()) {
                throw new FileNotFoundException("The file \"" + fullUsablePath + "\" does not exist.");
            }
            // startActivity(Intent.createChooser(firefoxIntent, "Open file using:"));
            firefoxIntent.setPackage("org.mozilla.firefox");
            startActivity(firefoxIntent);
            this.finish();
        } catch (final Exception e) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getApplicationContext());
            }
            builder.setTitle("Open In Firefox: Error")
                    .setMessage(e.getMessage())
                    .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).setPositiveButton("Send to Support", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    String fullUsablePath = new PathDecoder().getPathFromURI(intent.getData());
                    // emailIntent.setDataAndType(Uri.parse("mailto:"), "message/rfc822");
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"shivanand.pattanshetti@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[SUPPORT] Open In Firefox");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                            "Howdy Developer," + "\n"
                                    + "\n" + "Error Message: " + e.getMessage()
                                    + "\n" + "Received Path: " + intent.getData().getPath()
                                    + "\n" + "Parsed Path: " + fullUsablePath
                                    + "\n" + "File exists: " + new File(fullUsablePath.replaceFirst("file:///", "/")).exists()
                                    + "\n\n"
                    );
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        Toast.makeText(MainActivity.this, "Once the email client opens, click on send.", Toast.LENGTH_LONG).show();
                        startActivity(emailIntent);
                        MainActivity.this.finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Could not send the email. There are no email clients installed.", Toast.LENGTH_LONG).show();
                        MainActivity.this.finish();
                    }
                            /*
                            try {
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(MainActivity.this, "Could not send the email. There are no email clients installed.", Toast.LENGTH_LONG).show();
                                MainActivity.this.finish();
                            } catch(Exception e){
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                MainActivity.this.finish();
                            }
                            */
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }
}
