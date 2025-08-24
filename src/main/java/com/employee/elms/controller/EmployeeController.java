package com.employee.elms.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.elms.dto.request.AuthRequest;
import com.employee.elms.dto.request.EmployeeRequestDTO;
import com.employee.elms.dto.response.AuthResponse;
import com.employee.elms.dto.response.EmployeeResponseDTO;
import com.employee.elms.entity.Employee;
import com.employee.elms.repository.EmployeeRepository;
import com.employee.elms.security.JwtUtil;
import com.employee.elms.service.EmployeeService;
import com.employee.elms.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    // Register a new employee
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody EmployeeRequestDTO dto){
        EmployeeResponseDTO savedEmployee = employeeService.registerEmployee(dto);
        return ResponseEntity.ok(savedEmployee);
    }

    // Login and get JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest dto) {
        // 1. Check if the user exists
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(dto.getEmail());

        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials : Email not found");
        }

        // 2. If the user exists, check the password
        Employee employee = optionalEmployee.get();
        if (!passwordEncoder.matches(dto.getPassword(), employee.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials : Password does not match");
        }

        // 3. If both checks pass, generate and return the token
        String token = jwtUtil.generateToken(employee);
        return ResponseEntity.ok(new AuthResponse(token, employee.getRole().name()));
    }

    // Get current user's profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmail(authentication.getName()));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return ResponseEntity.ok("Logged out successfully");
    }

}
