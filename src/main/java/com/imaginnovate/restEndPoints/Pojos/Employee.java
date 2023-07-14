package com.imaginnovate.restEndPoints.Pojos;

import lombok.Data;

import java.util.List;

@Data
public class Employee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> phoneNumbers;
    private String doj;
    private double salary;

    public Employee() {
    }

    public Employee(String employeeId, String firstName, String lastName, String email, List<String> phoneNumbers, String doj, double salary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumbers = phoneNumbers;
        this.doj = doj;
        this.salary = salary;
    }

}
