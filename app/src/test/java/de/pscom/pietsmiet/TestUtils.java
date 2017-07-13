package de.pscom.pietsmiet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class TestUtils {

    public static String getResource(String path) throws IOException {
        return IOUtils.toString(TestUtils.class.getClassLoader().getResourceAsStream(path), "UTF-8");
    }

}