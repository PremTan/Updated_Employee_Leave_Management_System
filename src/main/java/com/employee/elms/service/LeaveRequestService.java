package com.employee.elms.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.employee.elms.dto.request.LeaveRequestDTO;
import com.employee.elms.dto.response.LeaveResponseDTO;
import com.employee.elms.entity.Employee;
import com.employee.elms.entity.LeaveRequest;
import com.employee.elms.entity.LeaveStatus;
import com.employee.elms.exception.BusinessRuleException;
import com.employee.elms.exception.InvalidLeaveDatesException;
import com.employee.elms.exception.ResourceNotFoundException;
import com.employee.elms.repository.EmployeeRepository;
import com.employee.elms.repository.LeaveRequestRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    // Apply for a leave
    @Transactional
    public LeaveResponseDTO applyLeave(Long employeeId, LeaveRequestDTO dto){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if(dto.getEndDate().isBefore(dto.getStartDate())){
            throw new InvalidLeaveDatesException("End date cannot be before start date");
        }
        
        boolean overlap = leaveRequestRepository.existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual
                (employeeId, dto.getEndDate(), dto.getStartDate());

        if(overlap){
            throw new InvalidLeaveDatesException("Leave request overlaps with an existing one!");
        }

        LeaveRequest leave = modelMapper.map(dto, LeaveRequest.class);
        leave.setEmployee(employee);
        leave.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepository.save(leave);

        // It will notify to admin
        emailService.notifyAdmin(
                "New Leave Request",
                "Employee " + saved.getEmployee().getEmployeeName() + " has applied for leave."
                );

        return modelMapper.map(saved, LeaveResponseDTO.class);
    }

    // Get all leaves of an employee
    public List<LeaveResponseDTO> getLeavesByEmployeeId(Long employeeId){
        List<LeaveRequest> leaves =  leaveRequestRepository.findAllByEmployeeIdOrderByAppliedAtDesc(employeeId);

        return leaves.stream()
                .map(lr -> modelMapper.map(lr, LeaveResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Get leaves by status
    public List<LeaveResponseDTO> getLeavesByStatus(LeaveStatus status){
        List<LeaveRequest> leaves = leaveRequestRepository.findByStatusOrderByAppliedAtDesc(status);

        return leaves.stream()
                .map(lr -> modelMapper.map(lr, LeaveResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Approve/Reject leave
    @Transactional
    public LeaveResponseDTO updateLeaveStatus(Long leaveId, LeaveStatus status){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        if (leaveRequest.getStatus() == status) {
            throw new BusinessRuleException("Leave request is already " + status);
        }

        if (leaveRequest.getStatus() == LeaveStatus.APPROVED || leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new BusinessRuleException("Leave request is already processed and cannot be changed.");
        }
        
        Employee employee = leaveRequest.getEmployee();

        if(status == LeaveStatus.APPROVED) {
            int days = leaveRequest.getNumberOfDays();

            if(employee.getRemainingLeaves() < days){
                throw new InvalidLeaveDatesException("Not enough leave balance for " + employee.getEmployeeName());
            }

            employee.setLeavesTaken(employee.getLeavesTaken() + days);
            employeeRepository.save(employee);
        }

        leaveRequest.setStatus(status);
        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        // It will Notify to Employee
        String subject = "Leave Request : "+ updated.getStatus() + " - " + updated.getEmployee().getEmployeeName();

        String body = "Dear " + updated.getEmployee().getEmployeeName() + ",\n\n"
                + "Your leave request has been reviewed.\n\n"
                + "📅 Your leave request from : " + updated.getStartDate() + " to " + updated.getEndDate() + "\n"
                + "📝 has been : " + updated.getStatus() + "\n\n"
                + "If you have any concerns regarding this decision, please reach out to the HR team.\n\n"
                + "Best Regards,\n"
                + "HR Department";

        emailService.sendMail(updated.getEmployee().getEmail(), subject, body);

        return modelMapper.map(updated, LeaveResponseDTO.class);
    }
}
