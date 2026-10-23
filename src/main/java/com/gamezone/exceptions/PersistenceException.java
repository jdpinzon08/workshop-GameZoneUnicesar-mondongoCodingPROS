package com.gamezone.exceptions;

/**
 * Thrown when an I/O or data access error occurs in the persistence layer
 */

public class PersistenceException extends GameZoneException{
    /**
     * Constructs the exception with contextual message and root cause.
     *
     * @param message the contextual error message
     * @param cause   the root exception (e.g., IOException)
     */

    public PersistenceException(String message, Throwable cause){
        super(message, "GAMEZONE ERROR #5: error en persistencias y de archivos");
        initCause(cause);
    }
}
