package com.devaffeine.file.sharing;

import java.nio.file.Path;

public class FileChunkChange {
    public enum Type {
        CHANGED,
        ADDED,
        REMOVED
    }

    Type type;

    Path file;

    long chunk;

    byte[] data;

    public FileChunkChange(Type type, Path file, long chunk) {
        this.type = type;
        this.file = file;
        this.chunk = chunk;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
