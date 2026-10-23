package com.gamezone.exceptions;

    /**
     * base unchecked exceptions for the gamezone system
     */
    public abstract class GameZoneException extends RuntimeException{

        private final String errorCode;

        /**
         * Constructs a new GameZoneException with the specified message
         *
         * @param message the descriptive error message
         */

        public GameZoneException(String message){
            super(message);
            this.errorCode = "Error general GAMEZONE ERROR #1:";
        }

        /**
         * Constructs a new GameZoneException with message and error code
         *
         * @param message   the descriptive error message
         * @param errorCode the unique error code
         */

        public GameZoneException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }

    }