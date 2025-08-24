package com.employee.elms.repository;

import com.employee.elms.entity.LeaveRequest;
import com.employee.elms.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Get all leave requests of an employee (most recent first)
    List<LeaveRequest> findAllByEmployeeIdOrderByAppliedAtDesc(Long employeeId);

    // Get leaves by status (e.g., PENDING / APPROVED / REJECTED), most recent first
    List<LeaveRequest> findByStatusOrderByAppliedAtDesc(LeaveStatus status);

    // if you want to check overlap in DB itself (optional)
    boolean existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId,
            LocalDate endDate,
            LocalDate startDate
    );

}
