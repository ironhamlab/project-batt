package ssafy.batt.domain.performance;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.domain.performance.Status.SCHEDULE;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.review.Review;
import ssafy.batt.domain.schedule.PerformanceSchedule;

@Entity
@Getter
@Table(name = "performance")
@NoArgsConstructor(access = PROTECTED)
public class Performance extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(STRING)
  private Genre genre;

  private String venueName;

  private String venueAddress;

  private Integer durationMinute;

  private Integer ageRestriction;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime performanceStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime performanceEndDate;

  private String posterImageUrl;

  private String descriptionUrl;

  @Enumerated(STRING)
  private Status status = SCHEDULE;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime bookingOpenDate;

  @OneToMany(mappedBy = "performance")
  private final List<PerformanceSchedule> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "performance")
  private final List<Review> reviews = new ArrayList<>();

  @Builder
  private Performance(String title, String description, Genre genre, String venueName,
      String venueAddress, Integer durationMinute, Integer ageRestriction, String posterImageUrl,
      String descriptionUrl, Status status, LocalDateTime bookingOpenDate) {
    this.title = title;
    this.description = description;
    this.genre = genre;
    this.venueName = venueName;
    this.venueAddress = venueAddress;
    this.durationMinute = durationMinute;
    this.ageRestriction = ageRestriction;
    this.posterImageUrl = posterImageUrl;
    this.descriptionUrl = descriptionUrl;
    this.status = status;
    this.bookingOpenDate = bookingOpenDate;
  }

  public void openBooking() {
    this.status = Status.OPEN;
  }
  
  public void endPerformance() {
    this.status = Status.END;
  }
}
