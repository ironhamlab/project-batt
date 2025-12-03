package ssafy.batt.domain.coin;

import static ssafy.batt.domain.coin.QCoin.coin;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.service.coin.response.CoinResponse;

@RequiredArgsConstructor
public class CoinRepositoryImpl implements CoinRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CoinResponse> getCoinInfo(Pageable pageable, Long memberId) {

    List<CoinResponse> content = queryFactory
        .select(Projections.constructor(CoinResponse.class,
            coin.id,
            coin.amount,
            coin.description,
            coin.transactionType,
            coin.remainCoin,
            coin.createdAt
        ))
        .from(coin)
        .where(coin.member.id.eq(memberId))
        .orderBy(coin.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long totalCount = queryFactory
        .select(coin.count())
        .from(coin)
        .where(coin.member.id.eq(memberId))
        .fetchOne();

    return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0L);
  }
}