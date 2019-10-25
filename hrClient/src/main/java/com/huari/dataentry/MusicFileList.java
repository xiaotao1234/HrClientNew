package com.huari.dataentry;

import java.io.File;
import java.util.List;

public class MusicFileList {
    public MusicFileList(List<File> stringList) {
        this.stringList = stringList;
    }

    public List<File> getStringList() {
        return stringList;
    }

    public void setStringList(List<File> stringList) {
        this.stringList = stringList;
    }

    List<File> stringList;

}
