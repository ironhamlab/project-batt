package ssafy.batt.domain.payment;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
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
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.refund.Refund;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor(access = PROTECTED)
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String paymentKey;

  private Integer amount;

  @Enumerated(STRING)
  private Method method;

  @Enumerated(STRING)
  private Status status = Status.PENDING;

  @OneToOne(mappedBy = "payment")
  private Refund refund;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  public Payment(Booking booking, String paymentKey, Integer amount, Method method) {
    this.booking = booking;
    this.paymentKey = paymentKey;
    this.amount = amount;
    this.method = method;
  }

  public void completePayment() {
    this.status = Status.COMPLETED;
  }

  public void cancelPayment() {
    this.status = Status.CANCELLED;
  }

  public boolean isCancelled() {
    return this.status == Status.CANCELLED;
  }
}