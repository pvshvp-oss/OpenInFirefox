package com.pattanshetti.shivanand.openinfirefox;

import android.net.Uri;
import android.os.Environment;

public class PathDecoder {
    private String scheme;
    private int schemeTokenIndex;
    private String host;
    private int hostTokenIndex;
    private String path;
    private String name;
    private int nameTokenIndex;
    private String extension;
    private boolean nullState;

    public void PathDecoder() {
        scheme = "";
        schemeTokenIndex = -1;
        host = "";
        hostTokenIndex = -1;
        path = "";
        name = "";
        nameTokenIndex = -1;
        extension = "";
        nullState = true;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isNull() {
        return nullState;
    }

    public String getPathFromURI(Uri URI) {
        if (URI == null) {
            return "";
        }
        nullState = false;

        String parsedPath = "";

        parsedPath = getPathFromDecodedString(URI.getPath());

        path = parsedPath;
        return parsedPath;
    }

    public String getPathFromURIString(String URIString) {
        if (URIString == null || URIString.isEmpty()) {
            return "";
        }
        nullState = false;

        String parsedPath = "";
        String decodedPath = "";
        String[] tokens = URIString.split("/");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.isEmpty()) {
                continue;
            } else if (token.indexOf(':') == token.length() - 1) {
                // Scheme string
                scheme = token.substring(0, token.length() - 1);
                schemeTokenIndex = i;
            } else if (i > 0 && tokens[i - 1].isEmpty()) {
                // Host string
                host = token;
                hostTokenIndex = i;
            } else if (i == tokens.length - 1) {
                // Name string
                name = token;
                nameTokenIndex = i;
                extension = name.substring(name.lastIndexOf('.') + 1);
                decodedPath += "/" + token;
            } else {
                decodedPath += "/" + token;
            }
        }

        parsedPath = getPathFromDecodedString(decodedPath);

        path = parsedPath;
        return parsedPath;
    }

    public String getPathFromDecodedString(String decodedString) {
        String parsedPath = decodedString;

        if (decodedString.startsWith("/root-path/")) {
            parsedPath = decodedString.replaceFirst("/root-path/", "/");
        } else if (decodedString.startsWith("/external_files/")) {
            parsedPath = decodedString.replaceFirst("/external_files/", Environment.getExternalStorageDirectory().getPath() + "/");
        } else if (decodedString.startsWith("/ext/")) {
            parsedPath = decodedString.replaceFirst("/ext/", Environment.getExternalStorageDirectory().getPath() + "/");
        } else if (decodedString.startsWith("/document/raw:/")) {
            parsedPath = decodedString.replaceFirst("/document/raw:/", "/");
        }

        if(decodedString.indexOf("/file/") == 0) {
            parsedPath = parsedPath.replace("/file/", "/");
        }

        parsedPath = "file://" + parsedPath;

        return parsedPath;
    }
}