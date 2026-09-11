package com.gamezone.model;

/**
 * Represents a seller or employee in the GameZone store.
 * Inherits common personal attributes from the abstract Person class.
 *
 * @author Desarrollador 2
 * @version 1.0
 */
public class Seller extends Person {

    private String employeeId;
    private String workShift;

    /**
     * Constructs a new Seller with the specified details.
     *
     * @param id          the unique identification of the seller
     * @param name        the full name of the seller
     * @param phoneNumber the contact phone number of the seller
     * @param employeeId  the unique employee code
     * @param workShift   the assigned work shift
     */
    public Seller(String id, String name, String phoneNumber, String employeeId, String workShift) {
        super(id, name, phoneNumber);
        this.employeeId = employeeId;
        this.workShift = workShift;
    }

    /**
     * Returns the employee ID of the seller.
     *
     * @return the employee ID string
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets or updates the employee ID of the seller.
     *
     * @param employeeId the new employee ID to set
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Returns the work shift of the seller.
     *
     * @return the work shift string
     */
    public String getWorkShift() {
        return workShift;
    }

    /**
     * Sets or updates the work shift of the seller.
     *
     * @param workShift the new work shift to set
     */
    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }
}