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

    public static Path writeToFile(final Path file, final String s) throws IOException {
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return null;
        }
        return file;
    }

    public static <T> Path writeMetaInfFile(Path folder, T object) throws IOException {
        Path metaDataFile = folder.resolve("meta.inf");
        if (Files.exists(metaDataFile)) {
            throw new IOException("meta.inf already exists");
        }
        Path metaDataFileTmp = folder.resolve(".meta.inf.tmp");
        if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
        if (FileUtils.writeToFile(metaDataFileTmp, JsonUtils.convertObjectToJsonStr(object)) != null) {
            Files.move(metaDataFileTmp, metaDataFile);
            if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
        }
        return metaDataFile;
    }


}
