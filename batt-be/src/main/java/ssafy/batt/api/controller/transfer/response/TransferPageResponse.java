package ssafy.batt.api.controller.transfer.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import ssafy.batt.api.service.transfer.response.TransferResponse;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class TransferPageResponse {

  private final PageInfo pageInfo;
  private final List<TransferResponse> transfers;

  @Getter
  @AllArgsConstructor(access = PRIVATE)
  public static class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
  }

  public static TransferPageResponse from(Page<TransferResponse> transferResponse) {

    PageInfo pageInfo = new TransferPageResponse.PageInfo(
        transferResponse.getTotalElements(),
        transferResponse.getTotalPages(),
        transferResponse.getNumber() + 1,
        transferResponse.getSize()
    );

    List<TransferResponse> transfers = transferResponse.getContent();
    return new TransferPageResponse(pageInfo, transfers);
  }
}
