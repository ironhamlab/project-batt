package ssafy.batt.api.controller.review.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class PerformanceReviewPageResponse {

  private final double averageRating;
  private final PageInfo pageInfo;
  private final List<ReviewResponse> reviews;

  @Getter
  @AllArgsConstructor(access = PRIVATE)
  public static class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
  }

  public static PerformanceReviewPageResponse from(Page<ReviewResponse> reviewResponse, double averageRating) {

    PageInfo pageInfo = new PageInfo(
        reviewResponse.getTotalElements(),
        reviewResponse.getTotalPages(),
        reviewResponse.getNumber() + 1,
        reviewResponse.getSize()
    );

    List<ReviewResponse> reviews = reviewResponse.getContent();
    return new PerformanceReviewPageResponse(averageRating, pageInfo, reviews);
  }
}