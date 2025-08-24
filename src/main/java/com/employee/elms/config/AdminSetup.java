package com.employee.elms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.employee.elms.entity.Employee;
import com.employee.elms.entity.Role;
import com.employee.elms.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AdminSetup {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.mail.admin}")
    private String adminEmail;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {

            // Check if admin already exists
            if (employeeRepository.findByEmail(adminEmail).isEmpty()) {
                Employee admin = new Employee();
                admin.setEmployeeName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);

                employeeRepository.save(admin);
                System.out.println("Default ADMIN created at "+adminEmail);
            }
        };
    }
}
