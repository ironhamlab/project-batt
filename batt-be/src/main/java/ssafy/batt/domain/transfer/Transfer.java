package ssafy.batt.domain.transfer;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.domain.transfer.Status.ACTIVE;
import static ssafy.batt.domain.transfer.Status.ENDED;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.bid.Bid;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.member.Member;

@Entity
@Getter
@Table(name = "transfer")
@NoArgsConstructor(access = PROTECTED)
public class Transfer extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private Member seller;

  private Integer currentHighestBid;

  private Instant transferEndDateTime;

  @Enumerated(STRING)
  private Status status = ACTIVE;

  @OneToMany(mappedBy = "transfer")
  private List<Bid> bids = new ArrayList<>();

  private Transfer(Booking booking, Member member, int currentHighestBid, Instant transferEndDateTime) {
    this.booking = booking;
    this.seller = member;
    this.currentHighestBid = currentHighestBid;
    this.transferEndDateTime = transferEndDateTime;
  }

  public static Transfer from(Member member, Booking booking, Instant transferEndDateTime) {
    return new Transfer(booking, member, 0, transferEndDateTime);
  }

  public void updateHighestBid(Integer newHighestBid) {
    this.currentHighestBid = newHighestBid;
  }

  public void closeTransfer() {
    this.status = ENDED;
  }
}