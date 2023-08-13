package com.devaffeine.file.sharing;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSharingServer {
    public Path serverPath;

    private Map<String, FileSharingChangesStore> stores;

    public FileSharingServer(Path serverPath) {
        this.serverPath = serverPath;
        this.stores = new HashMap<>();
    }

    public void applyChanges(String clientId, List<FileChunkChange> changes) {
        var store = stores.computeIfAbsent(clientId, x -> new FileSharingChangesStore(serverPath.resolve(clientId)));
        for(var change : changes) {

            store.addChange(change);
        }
    }

    public List<FileChunkChange> downloadChanges(String clientId, long from, long to) {
        var store = stores.computeIfAbsent(clientId, x -> new FileSharingChangesStore(serverPath.resolve(clientId)));
        return store.readChanges(from, to);
    }
}
