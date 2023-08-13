package com.devaffeine.file.sharing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSharingChangesStore {
    private static final Logger LOG = Logger.getLogger(FileSharingChangesStore.class.getName());

    Path path;

    AtomicLong counter = new AtomicLong();

    public FileSharingChangesStore(Path path) {
        this.path = path;
        try {
            Files.createDirectories(path);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void addChange(FileChunkChange change) {
        long id = counter.incrementAndGet();
        LOG.info("adding new change for client: " + path.getFileName() + " index: " + id);
        saveProperties(id, change);
        saveData(id, change.data);
    }

    private void saveData(long id, byte[] data) {
        try {
            Path changePath = path.resolve(id + ".dat");
            Files.deleteIfExists(changePath);
            Files.createFile(changePath);
            Files.write(changePath, data);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void saveProperties(long id, FileChunkChange change) {
        Path changePath = path.resolve(id + ".xml");
        Properties properties = new Properties();
        properties.setProperty("id", String.valueOf(id));
        properties.setProperty("type", change.type.toString());
        properties.setProperty("chunk", String.valueOf(change.chunk));
        properties.setProperty("file", change.file.toString());
        try {
            Files.deleteIfExists(changePath);
            Files.createFile(changePath);
            try (var os = new FileOutputStream(changePath.toFile())) {
                properties.storeToXML(os, "");
            }
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    List<FileChunkChange> readChanges(long from, long to) {
        List<FileChunkChange> lst = new ArrayList<>();
        for(long i = from; i < Math.min(to, counter.get()); i++) {
            lst.add(readChange(i));
        }
        return lst;
    }

    private FileChunkChange readChange(long index) {
        FileChunkChange change = readProperties(index);
        change.setData(readData(index));
        return change;
    }

    private byte[] readData(long index) {
        try {
            Path changePath = path.resolve(index + ".dat");
            return Files.readAllBytes(changePath);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    private FileChunkChange readProperties(long index) {
        Path changePath = path.resolve(index + ".xml");
        Properties properties = new Properties();
        try (var is = new FileInputStream(changePath.toFile())) {
            properties.loadFromXML(is);
            FileChunkChange.Type type = FileChunkChange.Type.valueOf(properties.getProperty("type"));
            long chunk = Long.valueOf(properties.getProperty("chunk"));
            Path file = Path.of(properties.getProperty("file"));
            return new FileChunkChange(type, file, chunk);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
