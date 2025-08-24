package com.employee.elms.dto.request;

import com.employee.elms.entity.LeaveType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

	@NotNull(message = "Leave type is required")
    private LeaveType leaveType;
	
	@NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
	
	@NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}
