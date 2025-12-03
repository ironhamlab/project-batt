package ssafy.batt.api.controller.transfer.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;

public record TransferDetailPageResponse(
    PageInfo pageInfo,
    List<TransferDetailResponse> transfers
) {

  @Getter
  @AllArgsConstructor
  public static class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
  }

  public static TransferDetailPageResponse from(Page<TransferDetailResponse> transferDetailResponse) {
    PageInfo pageInfo = new PageInfo(
        transferDetailResponse.getTotalElements(),
        transferDetailResponse.getTotalPages(),
        transferDetailResponse.getNumber() + 1,
        transferDetailResponse.getSize()
    );

    List<TransferDetailResponse> transferDetail = transferDetailResponse.getContent();
    return new TransferDetailPageResponse(pageInfo, transferDetail);
  }
}