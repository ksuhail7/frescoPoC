package com.suhailkandanur.fresco.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by suhail on 2016-12-09.
 */
public class ChecksumUtils {
    public static String sha1(String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String sha1(File file) throws IOException {
        return sha1(new FileInputStream(file));
    }

    public static String sha1(InputStream inputStream) throws IOException {
        return DigestUtils.sha1Hex(inputStream);
    }
}
