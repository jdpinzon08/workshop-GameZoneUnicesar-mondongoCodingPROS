package com.gamezone.exceptions;


/**
 * Thrown when an entity or resource is not found in the system
 */
public class ResourceNotFoundException extends GameZoneException{
    /**
     * Constructs the exception specifying the resource type and identifier
     *
     * @param resourceType the type of the entity (e.g., Product, Customer)
     * @param identifier   the requested identifier
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("El " + resourceType + " con identificador " + identifier + " no fue encontrado", "GAMEZONE ERROR #2: ");
    }

}
