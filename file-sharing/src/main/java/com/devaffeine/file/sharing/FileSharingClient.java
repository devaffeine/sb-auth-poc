package com.devaffeine.file.sharing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSharingClient {
    public static final Logger LOG = Logger.getLogger(LocalFileInfo.class.getName());
    private Path folder;

    private Map<Path, LocalFileInfo> files;

    private FileSharingServer server;

    private String clientId;

    public FileSharingClient(String clientId, Path folder, FileSharingServer server, ScheduledExecutorService executor) {
        this.folder = folder;
        this.files = new HashMap<>();
        this.clientId = clientId;
        this.server = server;
        executor.scheduleWithFixedDelay(this::uploadChanges, 10, 10, TimeUnit.SECONDS);
    }

    private void uploadChanges() {
        server.applyChanges(clientId, detectChanges());
        try {
            readAllFiles();
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private List<FileChunkChange> detectChanges() {
        List<FileChunkChange> changes = new ArrayList<>();
        try {
            Files.list(folder).forEach(x -> {
                try {
                    var file = folder.relativize(x);
                    var prev = files.get(file);
                    var current = new LocalFileInfo(x);
                    int minSize = 0;
                    if(prev != null) {
                        minSize = Math.min(current.chunks.size(), prev.chunks.size());
                        for (int i = 0; i < minSize; i++) {
                            if (!prev.chunks.get(i).hash.equals(current.chunks.get(i).hash)) {
                                var change = new FileChunkChange(FileChunkChange.Type.CHANGED, file, i);
                                change.setData(current.readChunk(i));
                                changes.add(change);
                            }
                        }
                        for (int i = minSize; i < prev.chunks.size(); i++) {
                            changes.add(new FileChunkChange(FileChunkChange.Type.REMOVED, file, i));
                        }
                    }
                    for (int i = minSize; i < current.chunks.size(); i++) {
                        var change = new FileChunkChange(FileChunkChange.Type.ADDED, file, i);
                        change.setData(current.readChunk(i));
                        changes.add(change);
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            });
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return changes;
    }

    public void readAllFiles() throws IOException {

        Files.list(folder).forEach(x -> {
            try {
                var file = folder.relativize(x);
                files.put(file, new LocalFileInfo(x));
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE,  ex.getMessage(), ex);
            }
        });
    }
}
