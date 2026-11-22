package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings in this order of priority:
 * 1. JVM system property  (-Dbrowser=firefox)
 * 2. Environment variable (BROWSER=firefox)  - handy for CI secrets
 * 3. config.properties
 */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config.properties", e);
        }
    }

    private Config() {
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv("APP_" + key.toUpperCase());
        }
        if (isBlank(value)) {
            value = PROPS.getProperty(key, "");
        }
        return value.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
