package com.huari.dataentry;

import java.io.File;

public class FileSearchData {

    File file;

    int start;

    public FileSearchData(File file, int start) {
        this.file = file;
        this.start = start;
    }

    public File getFile() {
        return file;
    }

    public int getStart() {
        return start;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
