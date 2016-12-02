package com.suhailkandanur.fresco.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by suhail on 2016-12-02.
 */
public class FileUtils {

    public static  Path writeToFile(final Path file, final String s) throws IOException {
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return null;
        }
        return file;
    }
}
