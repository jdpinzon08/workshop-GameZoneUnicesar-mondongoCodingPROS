package com.gamezone.exceptions;

/**
 * Thrown when a business rule or invariant is violated
 */

public class BusinessRuleException extends GameZoneException{
    /**
     *Constructs the exception with a message describing the business violation
     *
     * @param message the description of the business rule failure
     */


    public BusinessRuleException(String message){
        super(message, "se realizo una violacion de las reglas del negocio. \n GAMEZONE ERROR #3: violacion_reglasNegocio");
    }
}
