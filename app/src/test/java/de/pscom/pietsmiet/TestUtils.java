package de.pscom.pietsmiet;

import org.apache.commons.io.IOUtil;

import java.io.IOException;

public class TestUtils {

    public static String getResource(String path) throws IOException {
        return IOUtil.toString(TestUtils.class.getClassLoader().getResourceAsStream(path), "UTF-8");
    }

}