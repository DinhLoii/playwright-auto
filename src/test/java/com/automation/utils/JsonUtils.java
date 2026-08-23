package com.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Utility for reading JSON test data files.
 * Uses Jackson ObjectMapper for deserialization.
 *
 * WHY: Centralizes JSON parsing so test data classes don't
 * duplicate ObjectMapper creation and error handling.
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
        // Utility class
    }

    /**
     * Reads a JSON file from the classpath and deserializes it to a list of objects.
     *
     * @param filePath path relative to src/test/resources
     * @param clazz    the target class type
     * @return list of deserialized objects
     */
    public static <T> List<T> readList(String filePath, Class<T> clazz) {
        try (InputStream stream = JsonUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (stream == null) {
                log.error("JSON file not found on classpath: {}", filePath);
                return Collections.emptyList();
            }

            List<T> data = objectMapper.readValue(stream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));

            log.debug("Loaded {} records from {}", data.size(), filePath);
            return data;

        } catch (IOException e) {
            log.error("Failed to parse JSON file: {}", filePath, e);
            return Collections.emptyList();
        }
    }

    /**
     * Reads a JSON file and deserializes it to a single object.
     *
     * @param filePath path relative to src/test/resources
     * @param clazz    the target class type
     * @return deserialized object, or null on failure
     */
    public static <T> T readObject(String filePath, Class<T> clazz) {
        try (InputStream stream = JsonUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (stream == null) {
                log.error("JSON file not found on classpath: {}", filePath);
                return null;
            }
            return objectMapper.readValue(stream, clazz);
        } catch (IOException e) {
            log.error("Failed to parse JSON file: {}", filePath, e);
            return null;
        }
    }

    /**
     * Converts a Java object to a JSON string.
     * Useful for API test request bodies.
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("Failed to serialize object to JSON", e);
            return "{}";
        }
    }
}
