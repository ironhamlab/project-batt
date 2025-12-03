package ssafy.batt.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

}
