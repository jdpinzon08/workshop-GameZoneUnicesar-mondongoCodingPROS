package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for the persistence of Person objects using CSV format.
 * Handles saving and retrieving the list of people from a comma-separated file.
 *
 * @author Desarrollador 2
 * @version 1.0
 */
public class PersonRepository {

    private String filePath;

    /**
     * Constructs a new PersonRepository with the specified file path.
     *
     * @param filePath the path to the CSV file where people data will be stored
     */
    public PersonRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a list of Person objects to the CSV file.
     * Format for Customer: Customer,id,name,phoneNumber,email
     * Format for Seller: Seller,id,name,phoneNumber,employeeId,workShift
     *
     * @param people the list of persons to save
     */
    public void saveAll(List<Person> people) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Person person : people) {
                if (person instanceof Customer) {
                    Customer c = (Customer) person;
                    writer.write("Customer," + c.getId() + "," + c.getName() + "," + c.getPhoneNumber() + "," + c.getEmail());
                } else if (person instanceof Seller) {
                    Seller s = (Seller) person;
                    writer.write("Seller," + s.getId() + "," + s.getName() + "," + s.getPhoneNumber() + "," + s.getEmployeeId() + "," + s.getWorkShift());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving people data to CSV: " + e.getMessage());
        }
    }

    /**
     * Retrieves all Person objects stored in the CSV file.
     *
     * @return a list of persons, or an empty list if the file doesn't exist
     */
    public List<Person> findAll() {
        List<Person> people = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return people;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 5) continue;

                String type = parts[0];
                String id = parts[1];
                String name = parts[2];
                String phoneNumber = parts[3];

                if ("Customer".equalsIgnoreCase(type)) {
                    String email = parts[4];
                    people.add(new Customer(id, name, phoneNumber, email));
                } else if ("Seller".equalsIgnoreCase(type) && parts.length >= 6) {
                    String employeeId = parts[4];
                    String workShift = parts[5];
                    people.add(new Seller(id, name, phoneNumber, employeeId, workShift));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading people data from CSV: " + e.getMessage());
        }

        return people;
    }
}