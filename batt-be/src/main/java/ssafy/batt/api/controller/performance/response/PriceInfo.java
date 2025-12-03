package ssafy.batt.api.controller.performance.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import ssafy.batt.domain.seat.Grade;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PriceInfo(Integer vip, Integer r, Integer s, Integer a, Integer b) {

  public static PriceInfo from(Map<Grade, Integer> priceMap) {
    return new PriceInfo(
        priceMap.get(Grade.VIP),
        priceMap.get(Grade.R),
        priceMap.get(Grade.S),
        priceMap.get(Grade.A),
        priceMap.get(Grade.B)
    );
  }

  @JsonProperty("VIP")
  public Integer getVip() { return vip; }
  @JsonProperty("R")
  public Integer getR() { return r; }
  @JsonProperty("S")
  public Integer getS() { return s; }
  @JsonProperty("A")
  public Integer getA() { return a; }
  @JsonProperty("B")
  public Integer getB() { return b; }
}