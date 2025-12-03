package ssafy.batt.api.controller.review.request;

public record ReviewCreateRequest(
    String content,
    double rating
) {

}