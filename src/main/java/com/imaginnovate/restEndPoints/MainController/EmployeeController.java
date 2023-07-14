package com.imaginnovate.restEndPoints.MainController;

import com.imaginnovate.restEndPoints.Pojos.Employee;
import com.imaginnovate.restEndPoints.Pojos.TaxDeductionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private Map<String, Employee> employees = new HashMap<>();

    @PostMapping
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        if (validateEmployee(employee)) {
            employees.put(employee.getEmployeeId(), employee);
            logger.info("Employee added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body("Employee added successfully");
        } else {
            logger.info("Invalid employee data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee data");
        }
    }

    @GetMapping("/{employeeId}/tax-deduction")
    public ResponseEntity<TaxDeductionResponse> getTaxDeduction(@PathVariable String employeeId) {
        Employee employee = employees.get(employeeId);
        if (employee != null) {
            double yearlySalary = calculateYearlySalary(employee);
            double taxAmount = calculateTaxAmount(yearlySalary);
            double cessAmount = calculateCessAmount(yearlySalary);
            TaxDeductionResponse response = new TaxDeductionResponse(
                    employee.getEmployeeId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    yearlySalary,
                    taxAmount,
                    cessAmount
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private boolean validateEmployee(Employee employee) {
        String empId = employee.getEmployeeId();
        return employees.get(empId) == null;
    }

    //Consider the DOJ while calculating total salary(If the employee joined on May 16th, we should not include April
    // month salary and May month's 15 days salary in total salary)
    private double calculateYearlySalary(Employee employee) {

        String[] dateList = employee.getDoj().split("-");
        if (dateList[1].equals("05") && dateList[2].equals("16")) {
            double excludedSalary = employee.getSalary() - (employee.getSalary() / 2);
            logger.info("Excluded Salary:" + excludedSalary);
            return employee.getSalary() * 12 - excludedSalary;
        }
        return employee.getSalary() * 12;
    }

    private double calculateTaxAmount(double yearlySalary) {
        if (yearlySalary <= 250000) {
            return 0;
        } else if (yearlySalary <= 500000) {
            return (yearlySalary - 250000) * 0.05;
        } else if (yearlySalary <= 1000000) {
            return 12500 + (yearlySalary - 500000) * 0.1;
        } else {
            return 12500 + 50000 + (yearlySalary - 1000000) * 0.2;
        }
    }

    //Collect additional 2% cess for the amount more than 2500000
    // (ex: yearly salary is 2800000 then collect 2% cess for 300000)
    private double calculateCessAmount(double yearlySalary) {
        if (yearlySalary > 2500000) {
            return (yearlySalary - 2500000) * 0.02;
        } else {
            return 0;
        }
    }
}

