package com.employee.elms.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee.elms.dto.request.EmployeeRequestDTO;
import com.employee.elms.dto.response.EmployeeResponseDTO;
import com.employee.elms.entity.Employee;
import com.employee.elms.entity.Role;
import com.employee.elms.exception.EmailAlreadyExistsException;
import com.employee.elms.exception.ResourceNotFoundException;
import com.employee.elms.repository.EmployeeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // Register new employee
    @Transactional
    public EmployeeResponseDTO registerEmployee(EmployeeRequestDTO dto){
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + dto.getEmail());
        }

        Employee employee = modelMapper.map(dto, Employee.class);
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        employee.setRole(Role.EMPLOYEE);

        Employee saved = employeeRepository.save(employee);

        return modelMapper.map(saved, EmployeeResponseDTO.class);
    }

    // Get employee by id
    public EmployeeResponseDTO getEmployeeById(Long id){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return modelMapper.map(emp, EmployeeResponseDTO.class);
    }

    public EmployeeResponseDTO getEmployeeByEmail(String email){
        Employee emp =  employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
        return modelMapper.map(emp, EmployeeResponseDTO.class);
    }


    // Get employee by email (for login)
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }

    // Get all employees
    public List<EmployeeResponseDTO> getAllEmployees(){
        return employeeRepository.findAll().stream()
                .map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class))
                .collect(Collectors.toList());
    }
}
