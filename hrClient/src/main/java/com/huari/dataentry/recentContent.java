package com.huari.dataentry;

import android.util.Log;


import java.io.Serializable;

public class recentContent implements Serializable {
    @Override
    public String toString() {
        return file + filename + type;
    }

    private String file;
    private String filename;
    private int type;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public recentContent(String file, String filename, int type) {
        this.file = file;
        this.type = type;
        this.filename = filename;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        Log.d("xiao", String.valueOf(file.hashCode()));
        return file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof recentContent) {
            return filename .equals (((recentContent) obj).getFilename());
        }
        return false;
    }
}