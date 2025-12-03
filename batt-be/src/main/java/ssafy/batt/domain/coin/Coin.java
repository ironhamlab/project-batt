package ssafy.batt.domain.coin;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static ssafy.batt.domain.coin.TransactionType.BID_SUCCESS;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

@Entity
@Getter
@Table(name = "coin")
@NoArgsConstructor(access = PROTECTED)
public class Coin extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private Member member;

  private Integer amount;

  private Integer remainCoin;

  @Enumerated(STRING)
  private TransactionType transactionType;

  private String description;

  private Coin(Member member, Integer amount, Integer remainCoin, TransactionType transactionType, String description) {
    this.member = member;
    this.amount = amount;
    this.remainCoin = remainCoin;
    this.transactionType = transactionType;
    this.description = description;
  }

  public static Coin of(Member member, Integer amount, TransactionType transactionType, String description) {
    if (transactionType == BID_SUCCESS) {
      amount = -amount;
    }
    member.updateCoinBalance(amount);
    return new Coin(member, amount, member.getCoinBalance(), transactionType, description);
  }

  public static int calculateTransferRewardPoint(int highestBidPoint) {
    return highestBidPoint * 30 / 100;
  }
}