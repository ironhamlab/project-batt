package ssafy.batt.domain.seat;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static ssafy.batt.domain.seat.SeatStatus.AVAILABLE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.booingSeat.BookingSeat;
import ssafy.batt.domain.schedule.PerformanceSchedule;

@Entity
@Getter
@Table(name = "seat")
public class Seat extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer number;

  private Integer floor;

  private String zone;

  private Integer row;

  @Enumerated(STRING)
  private Grade grade;

  private Integer price;

  private Integer x;

  private Integer y;

  @Enumerated(STRING)
  private SeatStatus status = AVAILABLE;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_schedule_id", nullable = false)
  private PerformanceSchedule performanceSchedule;

  @OneToMany(mappedBy = "seat")
  private final List<BookingSeat> bookingSeats = new ArrayList<>();

  public void reserve() {
    this.status = SeatStatus.RESERVED;
  }
  
  public void release() {
    this.status = SeatStatus.AVAILABLE;
  }

  public String generateFormattingSeatNumber() {
    return String.format("%d층 %s구역 %d열 %d번",
        this.floor,
        this.zone,
        this.row,
        this.number
    );
  }

}

