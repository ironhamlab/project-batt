package ssafy.batt.api.controller.token.response;


public record IdentifierResponse(
    Long memberId,
    Integer remainCoin
) {

  public static IdentifierResponse of(Long memberId, Integer remainCoin) {
    return new IdentifierResponse(memberId, remainCoin);
  }
}