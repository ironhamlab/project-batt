package ssafy.batt.domain.schedule;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.domain.schedule.Status.SCHEDULED;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.performance.Performance;
import ssafy.batt.domain.seat.Seat;

@Entity
@Getter
@Table(name = "performance_schedule")
@NoArgsConstructor(access = PROTECTED)
public class PerformanceSchedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate performanceDate;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime performanceTime;

  private Integer totalSeat;

  private Integer availableSeat;

  @Enumerated(STRING)
  private Status status = SCHEDULED;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false)
  private Performance performance;

  @OneToMany(mappedBy = "performanceSchedule")
  private final List<Seat> seats = new ArrayList<>();

  @OneToMany(mappedBy = "performanceSchedule")
  private final List<Booking> bookings = new ArrayList<>();

  @Builder
  private PerformanceSchedule(LocalDate performanceDate, LocalTime performanceTime, Integer totalSeat,
      Integer availableSeat, Status status, LocalDateTime performanceStartDate,
      LocalDateTime performanceEndDate, Performance performance) {
    this.performanceDate = performanceDate;
    this.performanceTime = performanceTime;
    this.totalSeat = totalSeat;
    this.availableSeat = availableSeat;
    this.status = status;
    this.performance = performance;
  }
}