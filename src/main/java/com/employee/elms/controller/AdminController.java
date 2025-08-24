package com.employee.elms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.elms.entity.LeaveStatus;
import com.employee.elms.service.EmployeeService;
import com.employee.elms.service.LeaveRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmployeeService employeeService;
    private final LeaveRequestService leaveRequestService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(Authentication authentication) {
        return ResponseEntity.ok("Welcome ADMIN : " + authentication.getName());
    }

    // Get all employees
    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // View leaves by status (PENDING, APPROVED, REJECTED)
    @GetMapping("/leaves/status/{status}")
    public ResponseEntity<?> getLeavesByStatus(@PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.getLeavesByStatus(status));
    }

    // Approve/Reject leave
    @PutMapping("/leaves/{leaveId}/update-status/{status}")
    public ResponseEntity<?> updateLeaveStatus(@PathVariable Long leaveId,
                                               @PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.updateLeaveStatus(leaveId, status));
    }
}
