package ssafy.batt.domain.transfer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.controller.transfer.TransferSortType;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;
import ssafy.batt.api.service.transfer.response.TransferResponse;

public interface TransferRepositoryCustom {

  Page<TransferResponse> getPerformanceTransferTickets(Pageable pageable, TransferSortType sortType);

  Page<TransferDetailResponse> getPerformanceTransferTicketDetails(Pageable pageable, Long performanceId);

}
