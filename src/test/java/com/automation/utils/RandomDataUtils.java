package com.automation.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Generates random test data to avoid hardcoded values
 * and conflicts when tests run in parallel.
 *
 * WHY: On a shared demo site like OrangeHRM, multiple users
 * run tests simultaneously. Random data prevents collisions.
 */
public class RandomDataUtils {

    private static final Random RANDOM = new Random();

    private RandomDataUtils() {}

    /**
     * Generates a random first name like "TestUser_a3f2".
     */
    public static String randomFirstName() {
        return "TestUser_" + randomSuffix();
    }

    /**
     * Generates a random last name like "Auto_b7c1".
     */
    public static String randomLastName() {
        return "Auto_" + randomSuffix();
    }

    /**
     * Generates a random email like "test_a3f2b7c1@example.com".
     */
    public static String randomEmail() {
        return "test_" + randomSuffix() + randomSuffix() + "@example.com";
    }

    /**
     * Generates a random employee ID (4-digit number as string).
     */
    public static String randomEmployeeId() {
        return String.valueOf(1000 + RANDOM.nextInt(9000));
    }

    /**
     * Returns a short random hex suffix (4 characters).
     */
    public static String randomSuffix() {
        return UUID.randomUUID().toString().substring(0, 4);
    }

    /**
     * Returns a random integer between min (inclusive) and max (exclusive).
     */
    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(min, max);
    }
}
