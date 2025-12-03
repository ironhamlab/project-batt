import apiClient from "../axios";
import type { Transfer } from "../../types/Transfer";
import { useAuthStore } from "../../stores/authStore";

export interface TransfersResponse {
  transfers: Transfer[];
  pageInfo?: {
    totalPages: number;
    totalElements?: number;
    currentPage?: number;
    size?: number;
  };
}

export async function fetchTransfersByPerformance(
  performanceId: string,
  page: number,
  size: number,
): Promise<TransfersResponse> {
  const response = await apiClient.get("/api/v1/transfers", {
    params: { performanceId, page, size },
  });
  console.log(response.data);
  return response.data as TransfersResponse;
}

interface SSEHandlers {
  onOpen?: (this: EventSource, ev: Event) => void;
  onUpdate?: (transfer: Transfer) => void;
  onError?: (this: EventSource, ev: Event) => void;
}

export function openTransfersSSE(
  performanceId: string,
  handlers: SSEHandlers = {},
): EventSource {
  const baseURL = (apiClient.defaults.baseURL || "").replace(/\/$/, "");
  const url = `${baseURL}/api/v1/transfers/subscribe?performanceId=${encodeURIComponent(
    performanceId,
  )}`;
  const es = new EventSource(url);

  console.log("🔗 SSE 연결 시작:", url);

  es.onopen = (event) => {
    console.log("✅ SSE 연결 성공:", event);
    handlers.onOpen?.call(es, event);
  };

  es.onerror = (event) => {
    console.error("❌ SSE 연결 오류:", event);
    handlers.onError?.call(es, event);
  };

  es.addEventListener("update", (event: MessageEvent) => {
    console.log("📨 SSE update 이벤트 수신:", {
      type: event.type,
      data: event.data,
      lastEventId: event.lastEventId,
      origin: event.origin,
    });
    try {
      const payload: Transfer = JSON.parse(event.data);
      console.log("📊 파싱된 update 데이터:", payload);
      handlers.onUpdate?.(payload);
    } catch (e) {
      console.error("SSE update parse error", e);
    }
  });

  es.addEventListener("transfer-end", (event: MessageEvent) => {
    console.log("🏁 SSE transfer-end 이벤트 수신:", {
      type: event.type,
      data: event.data,
      lastEventId: event.lastEventId,
      origin: event.origin,
    });
    try {
      const payload: Transfer = JSON.parse(event.data);
      console.log("🔚 양도가 마감되었습니다:", payload);
      handlers.onUpdate?.(payload);
    } catch (e) {
      console.error("SSE transfer-end parse error", e);
    }
  });

  return es;
}

export interface BidRequest {
  coin: number;
}

export type BidResponse = Partial<Transfer> & {
  remainCoin?: number;
};

export async function createBid(
  transferId: number,
  bidData: BidRequest,
): Promise<BidResponse> {
  const response = await apiClient.post(
    `/api/v1/transfers/${transferId}/bids`,
    bidData,
  );

  console.log(response.data);

  const data: BidResponse = response.data;
  const remainCoin = data.remainCoin;
  console.log("data", data);
  console.log("remainCoin", remainCoin);
  if(remainCoin) {

    useAuthStore.getState().setCoin(remainCoin);
    }

  return data;
}
