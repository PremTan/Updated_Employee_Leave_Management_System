package com.employee.elms.dto.response;

import com.employee.elms.entity.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveResponseDTO {

    private Long id;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private String reason;
//    private int leavesRemains;
}
