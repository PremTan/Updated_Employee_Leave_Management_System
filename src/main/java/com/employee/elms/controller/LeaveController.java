package com.employee.elms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.elms.dto.request.LeaveRequestDTO;
import com.employee.elms.dto.response.EmployeeResponseDTO;
import com.employee.elms.entity.Employee;
import com.employee.elms.service.EmployeeService;
import com.employee.elms.service.LeaveRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;

    // Employee applies for leave
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveRequestDTO dto, Authentication authentication){
        String email = authentication.getName();
        EmployeeResponseDTO emp = employeeService.getEmployeeByEmail(email);
        Object leaveData = leaveRequestService.applyLeave(emp.getId(), dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Leave applied and email notification sent");
        response.put("data", leaveData);

        return ResponseEntity.ok(response);
    }

    // Employee views their own leaves
    @GetMapping("/my-leaves")
    public ResponseEntity<?> getMyLeaves(Authentication authentication){
        String email = authentication.getName();
        EmployeeResponseDTO emp = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(leaveRequestService.getLeavesByEmployeeId(emp.getId()));
    }

    // Get Leave Balance
    @GetMapping("/leave-balance")
    public ResponseEntity<?> getLeaveBalance(Authentication authentication){
        String email = authentication.getName();
         Employee emp = employeeService.getEmployeeEntityByEmail(email);
         return ResponseEntity.ok(Map.of(
        		  "remainingLeaves", emp.getRemainingLeaves(),
        		  "totalLeaves", emp.getTotalLeaves(),
        		  "leavesTaken", emp.getLeavesTaken()
        		));
    }
}
