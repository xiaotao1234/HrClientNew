package com.huari.dataentry;

public class HistoryDataDescripe {
    String name;
    long size;
    long lastModifne;
    String abslitPath;

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getLastModifne() {
        return lastModifne;
    }

    public String getAbslitPath() {
        return abslitPath;
    }

    public HistoryDataDescripe(String name, String abslitPath, long lastModifne, long size) {
        this.name = name;
        this.lastModifne = lastModifne;
        this.size = size;
        this.abslitPath = abslitPath;
    }
}
