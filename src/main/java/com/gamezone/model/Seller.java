package com.gamezone.model;

public class Seller extends Person {

    private String employeeId;
    private String workShift;

    public Seller(String id, String name, String phoneNumber, String employeeId, String workShift) {
        super(id, name, phoneNumber);
        this.employeeId = employeeId;
        this.workShift = workShift;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }
}
