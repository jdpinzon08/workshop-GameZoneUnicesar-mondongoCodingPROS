package com.gamezone.exceptions;

/**
 * throw when an input argument does not meet format, range, or requeried constraints
 */


public class InvalidDataException extends GameZoneException{

    /**
     * Constructs the exception detailing the invalid field and the reason.
     *
     * @param fieldName the name of the invalid field
     * @param reason    the reason explaining why the field is invalid
     */

    public InvalidDataException(String fieldName, String reason){
        super("El campo '" + fieldName + "' es invalido: " + reason, "GAMEZONE ERROR #4: dato_invalido");

    }
}
