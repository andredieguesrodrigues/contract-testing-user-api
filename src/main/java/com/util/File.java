package com.util;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class File {

    public static String readProperty(String property) {
        Properties prop;
        String value = null;
        Path root = Paths.get(".").normalize().toAbsolutePath();

        try {
            prop = new Properties();
            prop.load(new FileInputStream(new java.io.File("src/main/resources/application.properties")));
            value = prop.getProperty(property);

            if (value == null || value.isEmpty()) {
                throw new Exception("Value not set or empty");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
