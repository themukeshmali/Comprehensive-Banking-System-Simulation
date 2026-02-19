package com.banking.util;

import com.banking.model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for serializing and deserializing banking data to/from files.
 */
public class DataPersistence {
    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = "customers.dat";

    /**
     * Saves customer data to file using Java serialization.
     */
    @SuppressWarnings("unchecked")
    public static void saveCustomers(Map<String, Customer> customers) {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(Paths.get(DATA_DIR, CUSTOMERS_FILE).toFile()))) {
                oos.writeObject(new HashMap<>(customers));
                System.out.printf("✅ Data saved successfully. %d customer(s) stored.%n", customers.size());
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving data: " + e.getMessage());
        }
    }

    /**
     * Loads customer data from file.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Customer> loadCustomers() {
        Path filePath = Paths.get(DATA_DIR, CUSTOMERS_FILE);

        if (!Files.exists(filePath)) {
            System.out.println("ℹ No saved data found. Starting fresh.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            Map<String, Customer> customers = (Map<String, Customer>) ois.readObject();
            System.out.printf("✅ Data loaded successfully. %d customer(s) restored.%n", customers.size());
            return customers;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Saves any serializable object to a file.
     */
    public static void saveObject(Object obj, String filename) {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(Paths.get(DATA_DIR, filename).toFile()))) {
                oos.writeObject(obj);
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Loads a serializable object from a file.
     */
    public static Object loadObject(String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);

        if (!Files.exists(filePath)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if data files exist.
     */
    public static boolean dataExists() {
        return Files.exists(Paths.get(DATA_DIR, CUSTOMERS_FILE));
    }

    /**
     * Deletes all saved data.
     */
    public static void clearData() {
        try {
            Path filePath = Paths.get(DATA_DIR, CUSTOMERS_FILE);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("✅ All saved data cleared.");
            }
        } catch (IOException e) {
            System.err.println("❌ Error clearing data: " + e.getMessage());
        }
    }
}
