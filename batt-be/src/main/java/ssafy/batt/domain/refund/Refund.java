package ssafy.batt.domain.refund;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.domain.refund.Status.PENDING;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.payment.Payment;

@Entity
@Getter
@Table(name = "refund")
@NoArgsConstructor(access = PROTECTED)
public class Refund extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  private Integer refundAmount;

  private Integer cancellationFee;

  private String reason;

  @Enumerated(STRING)
  private Status status = PENDING;

  public Refund(Payment payment, Integer refundAmount, Integer cancellationFee, String reason) {
    this.payment = payment;
    this.refundAmount = refundAmount;
    this.cancellationFee = cancellationFee;
    this.reason = reason;
  }

  public void completeRefund() {
    this.status = Status.COMPLETED;
  }
}
