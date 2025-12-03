package ssafy.batt.domain.seat;

import java.util.Map;

public enum Grade {
  VIP, R, S, A, B;

  public static Map<Grade, Integer> getDefaultPriceMap(Map<Grade, Integer> priceMap) {
    if (priceMap == null || priceMap.isEmpty()) {
      return Map.of(
          Grade.VIP, 180000,
          Grade.R, 140000,
          Grade.S, 110000,
          Grade.A, 80000
      );
    }
    return priceMap;
  }
}