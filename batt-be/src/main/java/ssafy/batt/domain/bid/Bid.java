package ssafy.batt.domain.bid;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.transfer.Transfer;

@Entity
@Getter
@Table(name = "bid")
@NoArgsConstructor(access = PROTECTED)
public class Bid extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transfer_id", nullable = false)
  private Transfer transfer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bidder_id", nullable = false)
  private Member bidder;

  private Integer bidAmount;

  private Bid(Transfer transfer, Member bidder, Integer bidAmount) {
    this.transfer = transfer;
    this.bidder = bidder;
    this.bidAmount = bidAmount;
  }

  public static Bid of(Transfer transfer, Member bidder, Integer bidAmount) {
    return new Bid(transfer, bidder, bidAmount);
  }
}