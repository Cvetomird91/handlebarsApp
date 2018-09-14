package handlebarstest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class FilesHandler {

    private final Map<String, String> readFiles = new HashMap<>();
    private final Map<String, String> writableFiles = new HashMap<>();
    private Charset charset = StandardCharsets.UTF_8;

    private boolean checkPathReadingValidity(String toCheck) {
        Path path = Paths.get(toCheck);

        return Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path);
    }

    private String fileToString(String toRead) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(toRead));
            return new String(fileBytes, charset);
        } catch (Exception e) {
            return "";
        }
    }

    boolean handleReadableFile(String id, String path) {
        if (!checkPathReadingValidity(path)) return false;
        String content = fileToString(path);
        if (!content.isEmpty()) {
            readFiles.put(id, content);
            return true;
        } else return false;
    }

    boolean handleWritableFile(String id, String path) {
        Path path1 = Paths.get(path);

        try {
            if (Files.exists(path1)) {
                new PrintWriter(new File(path)).close();
            } else {
                Files.createFile(path1);
            }
        } catch (IOException e) {
            return false;
        }

        if (Files.isWritable(path1) && Files.isRegularFile(path1)) {
            writableFiles.put(id, path);
            return true;
        } else return false;
    }

    boolean appendToWritableFile(String id, String toAppend) {
        Path path = Paths.get(writableFiles.get(id));

        try {
            Files.write(path, Collections.singleton(toAppend), charset);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    String getReadFile(String id) {
        return readFiles.get(id);
    }

    String getWritableFile(String id) {
        return writableFiles.get(id);
    }

    void cleanUp() {
        writableFiles.clear();
        readFiles.clear();
    }

}
