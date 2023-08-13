package com.devaffeine.file.sharing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class LocalFileInfo {
    public static final Logger LOG = Logger.getLogger(LocalFileInfo.class.getName());

    public static final int CHUNK_SIZE = 64 * 1024 * 1024; // 64MB

    public byte[] readChunk(long chunkIndex) throws IOException {
        try(var channel = FileChannel.open(file, StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocateDirect(CHUNK_SIZE);
            channel.position(chunkIndex * CHUNK_SIZE);
            int readed = channel.read(buffer);
            buffer.flip();
            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            return arr;
        }
    }

    public static class Chunk {
        String hash;

        public Chunk(String hash) {
            this.hash = hash;
        }
    }

    Path file;

    LocalDateTime lastModified;

    long size;

    List<Chunk> chunks;

    public LocalFileInfo(Path file) throws IOException, NoSuchAlgorithmException {
        this.file = file;
        this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault());
        this.size = Files.size(file);
        this.chunks = new ArrayList<>();
        try(var channel = FileChannel.open(file, StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocateDirect(CHUNK_SIZE);
            int chunkIndex = 0;
            while (true) {
                buffer.clear();
                int readed = channel.read(buffer);
                buffer.flip();
                byte[] arr = new byte[buffer.remaining()];
                buffer.get(arr);
                chunks.add(new Chunk(calcHash(arr, chunkIndex)));
                chunkIndex++;

                if (readed < CHUNK_SIZE) {
                    return;
                }
            }
        }
    }

    private String calcHash(byte[] bytes, int chuckIndex) throws NoSuchAlgorithmException {
        long time = System.currentTimeMillis();
        var md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(bytes);
        String result = Base64.getEncoder().encodeToString(hash);
        time = System.currentTimeMillis() - time;
        LOG.info("Calculating hash for chunk: " + chuckIndex + " in file " + file.getFileName() + " time: " + time + "ms result: " + result);
        return result;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
}
