package ssafy.batt.api.service.bid.request;

public record BidEventCreateServiceRequest(
    Long transferId,
    Integer HighestBidCoin
) {

}