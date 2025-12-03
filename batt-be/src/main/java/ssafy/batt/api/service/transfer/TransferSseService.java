package ssafy.batt.api.service.transfer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferSseService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

  public SseEmitter subscribe(Long performanceId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    try {
      emitter.send(
          SseEmitter.event()
              .name("connect")
              .data("Connection established with performanceId: " + performanceId));
    } catch (IOException e) {
      log.error("SSE 연결 시 초기 데이터 전송 실패", e);
    }

    emitter.onCompletion(() -> removeEmitter(performanceId, emitter));
    emitter.onTimeout(() -> removeEmitter(performanceId, emitter));
    emitter.onError(e -> {
      log.error("SSE 통신 중 오류 발생", e);
      removeEmitter(performanceId, emitter);
    });

    emitters.computeIfAbsent(performanceId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    log.info("새로운 SSE 연결 등록: performanceId={}, 총 연결 수={}",
        performanceId, emitters.get(performanceId).size());
    return emitter;
  }

  public void sendUpdate(Long performanceId, TransferDetailResponse data) {
    List<SseEmitter> emitterList = emitters.get(performanceId);
    if (emitterList != null && !emitterList.isEmpty()) {
      emitterList.removeIf(emitter -> {
        try {
          emitter.send(SseEmitter.event().name("update").data(data));
          return false;
        } catch (IOException e) {
          log.error("SSE 데이터 전송 실패: performanceId={}", performanceId, e);
          emitter.completeWithError(e);
          return true;
        }
      });
      log.info("SSE 데이터 전송: performanceId={}, 전송된 클라이언트 수={}",
          performanceId, emitterList.size());
    }
  }

  public void sendTransferEnd(Long performanceId, TransferDetailResponse data) {
    List<SseEmitter> emitterList = emitters.get(performanceId);
    if (emitterList != null && !emitterList.isEmpty()) {
      emitterList.removeIf(emitter -> {
        try {
          emitter.send(SseEmitter.event().name("transfer-end").data(data));
          return false;
        } catch (IOException e) {
          log.error("SSE 마감 알림 전송 실패: performanceId={}", performanceId, e);
          emitter.completeWithError(e);
          return true;
        }
      });
      log.info("양도 마감 SSE 알림 전송: performanceId={}, transferId={}, 전송된 클라이언트 수={}",
          performanceId, data.getTransferId(), emitterList.size());
    }
  }

  private void removeEmitter(Long performanceId, SseEmitter emitter) {
    List<SseEmitter> emitterList = emitters.get(performanceId);
    if (emitterList != null) {
      emitterList.remove(emitter);
      if (emitterList.isEmpty()) {
        emitters.remove(performanceId);
      }
      log.info("SSE 연결 해제: performanceId={}, 남은 연결 수={}",
          performanceId, emitterList.size());
    }
  }
}