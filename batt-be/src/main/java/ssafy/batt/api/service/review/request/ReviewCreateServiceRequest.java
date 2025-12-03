package ssafy.batt.api.service.review.request;

import ssafy.batt.api.controller.review.request.ReviewCreateRequest;

public record ReviewCreateServiceRequest(
    String content,
    double rating
) {

  public static ReviewCreateServiceRequest from(ReviewCreateRequest request) {
    return new ReviewCreateServiceRequest(request.content(), request.rating());
  }
}