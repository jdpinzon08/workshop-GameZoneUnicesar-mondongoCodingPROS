package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;
import java.util.List;

/**
 * Static validation utility for person operations.
 */
public final class PersonValidator {

    private PersonValidator() {
        // Prevent instantiation
    }

    /**
     * Validates general personal data format.
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("id", "no puede estar vacío");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("name", "no puede estar vacío");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidDataException("phone", "no puede estar vacío");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidDataException("email", "no puede estar vacío");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("email", "debe contener '@' y un punto");
        }
    }

    /**
     * Validates the required identity and contact fields for a person type that has no email field.
     *
     * @param id the person's unique identifier
     * @param name the person's name
     * @param phone the person's phone number
     */
    public static void validatePersonData(String id, String name, String phone) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("id", "no puede estar vacío");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("name", "no puede estar vacío");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidDataException("phone", "no puede estar vacío");
        }
    }

    /**
     * Validates that the person instance is not null.
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("cliente/vendedor", personId);
        }
    }

    /**
     * Validates that no other person shares the same identifier in the given list.
     */
    public static void validateUniquePerson(List<Person> existing, String newId) {
        if (existing != null && newId != null) {
            for (Person p : existing) {
                if (newId.trim().equalsIgnoreCase(p.getId().trim())) {
                    throw new BusinessRuleException("Ya existe una persona registrada con el identificador: " + newId);
                }
            }
        }
    }
}
