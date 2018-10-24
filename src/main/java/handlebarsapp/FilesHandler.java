package handlebarsapp;

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
import java.util.logging.FileHandler;

public class FilesHandler {

    private final Map<String, String> readFiles = new HashMap<>();
    private final Map<String, String> writableFiles = new HashMap<>();
    public static Charset charset = StandardCharsets.UTF_8;

    private boolean invalidInput = false;

    public void handleFilesFromInput(Map<String, String> parsedInputMap) throws IOException {
        for (String id : new String[]{"json", "hbs"}) //Readable files
            if (!handleReadableFile(id, parsedInputMap.get(id))) {
                System.err.println("Invalid " + id + " file: " + parsedInputMap.get(id));
                invalidInput = true;
            }

        //Writable files
        if (!handleWritableFile("output", parsedInputMap.get("output"))) {
            System.err.println("Invalid json file: " + parsedInputMap.get("output"));
            invalidInput = true;
        }
    }

    private boolean checkPathReadingValidity(String toCheck) {
        Path path = Paths.get(toCheck);

        return Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path);
    }

    public static String readContentFromFile(String path) throws IOException {
        return readContentFromFile(path, charset);
    }

    public static String readContentFromFile(String path, Charset charset) throws IOException {
        byte[] rawBytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/" + path));
        return new String(rawBytes, charset);
    }

    public boolean handleReadableFile(String id, String path) throws IOException {
        if (!checkPathReadingValidity(path)) return false;
        String content = FilesHandler.readContentFromFile(path);
        if (!content.isEmpty()) {
            readFiles.put(id, content);
            return true;
        } else return false;
    }

    public boolean handleWritableFile(String id, String path) {
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

    public boolean appendToWritableFile(String id, String toAppend) {
        Path path = Paths.get(writableFiles.get(id));

        try {
            Files.write(path, Collections.singleton(toAppend), charset);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String getReadFile(String id) {
        return readFiles.get(id);
    }

    public String getWritableFile(String id) {
        return writableFiles.get(id);
    }

    public void cleanUp() {
        writableFiles.clear();
        readFiles.clear();
    }

    public boolean hasInvalidInput(){
        return invalidInput;
    }

}
