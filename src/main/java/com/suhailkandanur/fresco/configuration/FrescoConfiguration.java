package com.suhailkandanur.fresco.configuration;

import org.springframework.stereotype.Component;

/**
 * Created by suhail on 2016-12-02.
 */
@Component
public class FrescoConfiguration {

    public String getFileSystem() {
        return "/Users/suhail/tmp/fresco/fs1"; //TODO: hardcoded filesystem path, externalize using config server & client
    }
}
