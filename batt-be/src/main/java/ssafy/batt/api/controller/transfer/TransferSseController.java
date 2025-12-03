package ssafy.batt.api.controller.transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.batt.api.service.transfer.TransferSseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferSseController {

  private final TransferSseService transferSseService;

  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribePerformanceTransferTickets(
      @RequestParam Long performanceId
  ) {
    log.info("SSE 구독 요청");
    return transferSseService.subscribe(performanceId);
  }
}
