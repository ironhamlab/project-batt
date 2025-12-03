package ssafy.batt.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

}
