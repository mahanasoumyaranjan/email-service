package com.cglia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cglia.entity.Employee;
import com.cglia.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmailService emailService;

	public Employee saveEmployee(Employee employee) {
		Employee savedEmployee = employeeRepository.save(employee);

		try {
			String password = "password12345678"; 
			String filePath = "C:\\Users\\HP\\3D Objects\\Test Result-1.pdf";
			emailService.sendEmailWithEncryptedAttachment(employee.getEmail(), "Welcome",
					"Hello " + employee.getName() + ", welcome to the company!", filePath, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedEmployee;
	}

	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id).orElse(null);
	}

	public Employee updateEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}

	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}

	// This method can be used for testing decryption
	public void testDecryption(String password, String encryptedFilePath, String decryptedFilePath) {
		try {
			emailService.decryptFile(password, encryptedFilePath, decryptedFilePath);
			System.out.println("Decryption successful.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Decryption failed.");
		}
	}
}
