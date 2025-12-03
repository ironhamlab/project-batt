package ssafy.batt.domain.performance;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceRepositoryCustom {

  @Query("SELECT p FROM Performance p WHERE p.status = :status AND p.bookingOpenDate <= :currentTime")
  List<Performance> findByStatusAndBookingOpenDateBefore(@Param("status") Status status, @Param("currentTime") LocalDateTime currentTime);

  @Query("SELECT p FROM Performance p WHERE p.status IN (:statuses) AND p.performanceEndDate < :endDateTime")
  List<Performance> findByStatusInAndPerformanceEndDateBefore(@Param("statuses") List<Status> statuses, @Param("endDateTime") LocalDateTime endDateTime);
}
