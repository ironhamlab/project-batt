package ssafy.batt.domain.member;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.Instant.*;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.common.constant.ConstantUtil.SIGN_UP_POINT;
import static ssafy.batt.common.exception.coin.CoinErrorCode.COIN_INSUFFICIENT;
import static ssafy.batt.domain.member.Role.USER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.api.service.auth.info.SocialUserInfo;
import ssafy.batt.common.exception.coin.CoinException;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.bid.Bid;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.coin.Coin;
import ssafy.batt.domain.review.Review;
import ssafy.batt.domain.transfer.Transfer;


@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Enumerated(STRING)
  private Sex sex;

  private String birth;

  private String phoneNumber;

  @Enumerated(STRING)
  private ProviderType providerType;

  private String providerId;

  private Integer coinBalance = 0;

  private Boolean isActive = true;

  private Instant deletedAt;

  @Enumerated(STRING)
  private Role role = USER;

  @OneToMany(mappedBy = "member")
  private final List<Booking> bookings = new ArrayList<>();

  @OneToMany(mappedBy = "seller")
  private final List<Transfer> transfers = new ArrayList<>();

  @OneToMany(mappedBy = "bidder")
  private final List<Bid> bids = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Coin> coinTransactions = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Review> reviews = new ArrayList<>();

  @Builder
  private Member(String email, String name, Sex sex, String birth, String phoneNumber,
      ProviderType providerType, String providerId, Integer coinBalance) {
    this.email = email;
    this.name = name;
    this.sex = sex;
    this.birth = birth;
    this.phoneNumber = phoneNumber;
    this.providerType = providerType;
    this.providerId = providerId;
    this.coinBalance = 0;
  }

  public static Member from(SocialUserInfo userInfo) {
    return Member.builder()
        .email(userInfo.getEmail())
        .name(userInfo.getName())
        .sex(userInfo.getGender())
        .birth(userInfo.getBirth())
        .phoneNumber(userInfo.getPhoneNumber())
        .providerType(userInfo.getProviderType())
        .providerId(userInfo.getProviderId())
        .coinBalance(SIGN_UP_POINT)
        .build();
  }

  public void updateCoinBalance(int amount) {
    if (this.coinBalance + amount < 0) {
      throw new CoinException(COIN_INSUFFICIENT);
    }
    this.coinBalance += amount;
  }

  public void softDelete() {
    this.isActive = false;
    this.deletedAt = now();
  }
}