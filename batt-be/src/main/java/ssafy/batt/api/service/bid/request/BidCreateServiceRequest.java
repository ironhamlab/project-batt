package ssafy.batt.api.service.bid.request;

import ssafy.batt.api.controller.bid.request.BidCreateRequest;

public record BidCreateServiceRequest(
    Integer coin
) {

  public static BidCreateServiceRequest from(BidCreateRequest request) {
    return new BidCreateServiceRequest(request.coin());
  }
}