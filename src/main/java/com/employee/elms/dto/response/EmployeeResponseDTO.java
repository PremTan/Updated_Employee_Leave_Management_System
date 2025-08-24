package com.employee.elms.dto.response;

import lombok.Data;

@Data
public class EmployeeResponseDTO {

    private Long id;
    private String employeeName;
    private String email;
    private String role;
}
