package ssafy.batt.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {

}
