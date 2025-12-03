package ssafy.batt.domain.admin;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.member.Member;

@Entity
@Getter
@Table(name = "admin_action_log")
public class AdminActionLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", nullable = false)
  private Member admin;

  @Enumerated(STRING)
  private ActionType actionType;

  @Enumerated(STRING)
  private TargetType targetType;

  private String description;
}
